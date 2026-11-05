import { readFile, stat } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..", "..", "..");
const projectSlug = "accuratefpsplus";
const projectEndpoint = "https://api.modrinth.com/v2";
const fabricApi = { project_id: "P7dR8mSH", dependency_type: "required" };
const optionalFabricDependencies = [
	{ project_id: "9s6osm5g", dependency_type: "optional" },
	{ project_id: "1eAoo2KR", dependency_type: "optional" },
	{ project_id: "mOgUt4GM", dependency_type: "optional" },
	{ project_id: "AANobbMI", dependency_type: "optional" },
	{ project_id: "xgdFs8ct", dependency_type: "optional" }
];
const optionalNeoForgeDependencies = [
	{ project_id: "9s6osm5g", dependency_type: "optional" },
	{ project_id: "1eAoo2KR", dependency_type: "optional" },
	{ project_id: "AANobbMI", dependency_type: "optional" }
];
// These Sodium NeoForge builds are documented as crashing on Minecraft 1.21.11.
const neoForge12111Incompatibilities = [
	{ version_id: "sC6wj32u", dependency_type: "incompatible" },
	{ version_id: "sVLtC9EW", dependency_type: "incompatible" }
];

// Keep this matrix aligned with the release jars and compatibility table.
export function getReleaseTargets(version) {
	const matrix = [
		{ minecraft: "1.21.1", gameVersions: ["1.21.1"] },
		{ minecraft: "1.21.11", gameVersions: ["1.21.11"] },
		{ minecraft: "26.1.x", gameVersions: ["26.1", "26.1.1", "26.1.2"] },
		{ minecraft: "26.2", gameVersions: ["26.2"] },
		{ minecraft: "26.3", gameVersions: ["26.3"] }
	];

	return matrix.flatMap(({ minecraft, gameVersions }) =>
		["fabric", "neoforge"].map((loader) => {
			const target = {
				minecraft,
				gameVersions,
				loader,
				file: `accuratefpsplus-${minecraft === "26.1.x" ? "26.1.2" : minecraft}-${version}-${loader}.jar`,
				versionNumber: `${version}-${loader}-mc${minecraft.replaceAll(".", "-")}`
			};
			target.dependencies = [
				...(loader === "fabric" && minecraft.startsWith("1.21.") ? [fabricApi] : []),
				...(loader === "fabric" ? optionalFabricDependencies : optionalNeoForgeDependencies),
				...(loader === "neoforge" && minecraft === "1.21.11" ? neoForge12111Incompatibilities : [])
			];
			return target;
		})
	);
}

export function extractChangelog(changelog, version, allowDevelopment = false) {
	const lines = changelog.split(/\r?\n/);
	const headingIndex = lines.findIndex((line) => {
		const match = line.match(/^##\s+(\S+)(.*)$/);
		return match?.[1] === version;
	});

	if (headingIndex < 0) {
		throw new Error(`CHANGELOG.md has no section for ${version}`);
	}

	const heading = lines[headingIndex];
	if (!allowDevelopment && /\bin development\b/i.test(heading)) {
		throw new Error(`CHANGELOG.md still marks ${version} as in development`);
	}

	let end = headingIndex + 1;
	while (end < lines.length && !/^##\s+/.test(lines[end])) {
		end++;
	}
	const notes = lines.slice(headingIndex + 1, end).join("\n").trim();
	if (!notes) {
		throw new Error(`CHANGELOG.md has an empty section for ${version}`);
	}
	return notes;
}

export function createVersionPayload(version, target, changelog, projectId) {
	return {
		name: `AccurateFPS+ ${version} - Minecraft ${target.minecraft} (${target.loader === "fabric" ? "Fabric" : "NeoForge"})`,
		version_number: target.versionNumber,
		changelog,
		dependencies: target.dependencies,
		game_versions: target.gameVersions,
		version_type: "release",
		loaders: [target.loader],
		featured: false,
		status: "listed",
		project_id: projectId,
		file_parts: ["artifact"],
		primary_file: "artifact",
		environment: "client_only"
	};
}

export function createDraftTestPayload(version, target, changelog, projectId, runId) {
	if (!/^\d+$/.test(runId)) {
		throw new Error("A numeric GitHub Actions run ID is required for a draft test");
	}

	const payload = createVersionPayload(version, target, changelog, projectId);
	payload.name = `TEST ONLY - ${payload.name}`;
	payload.version_number = `${version}-test-${runId}`;
	payload.changelog = `TEST ONLY: draft metadata validation. Do not install or redistribute this file.\n\n${changelog}`;
	payload.status = "draft";
	return payload;
}

function readVersion() {
	return readFile(path.join(root, "gradle.properties"), "utf8").then((properties) => {
		const match = properties.match(/^version=(.+)$/m);
		if (!match) {
			throw new Error("gradle.properties does not define the root version");
		}
		return match[1].trim();
	});
}

function verifyReleaseTag(version) {
	const tag = process.env.GITHUB_REF_NAME;
	if (tag && tag.replace(/^v/, "") !== version) {
		throw new Error(`Release tag ${tag} does not match Gradle version ${version}`);
	}
}

async function loadReleasePlan(allowDevelopment) {
	const version = await readVersion();
	verifyReleaseTag(version);

	const [changelog, targets] = await Promise.all([
		readFile(path.join(root, "CHANGELOG.md"), "utf8"),
		Promise.resolve(getReleaseTargets(version))
	]);
	const notes = extractChangelog(changelog, version, allowDevelopment);

	for (const target of targets) {
		const artifact = path.join(root, "build", "libs", target.file);
		try {
			await stat(artifact);
		} catch {
			throw new Error(`Expected release jar is missing: ${path.relative(root, artifact)}`);
		}
	}

	return { version, notes, targets };
}

async function apiRequest(url, token, options = {}) {
	const response = await fetch(url, {
		...options,
		headers: {
			...(options.headers ?? {}),
			...(token ? { Authorization: token } : {})
		}
	});
	if (!response.ok) {
		const details = (await response.text()).slice(0, 1000);
		throw new Error(`Modrinth API request failed (${response.status}): ${details}`);
	}
	return response.json();
}

async function listProjectVersions(projectId, token) {
	const versions = [];
	for (let offset = 0; ; offset += 100) {
		const page = await apiRequest(
			`${projectEndpoint}/project/${projectId}/version?limit=100&offset=${offset}`,
			token
		);
		versions.push(...page);
		if (page.length < 100) {
			return versions;
		}
	}
}

function dependencyKey(dependency) {
	return `${dependency.dependency_type}:${dependency.project_id ?? ""}:${dependency.version_id ?? ""}`;
}

function matchesExistingVersion(existing, payload, fileName) {
	return (
		existing.name === payload.name &&
		existing.version_number === payload.version_number &&
		existing.changelog === payload.changelog &&
		existing.version_type === payload.version_type &&
		existing.environment === payload.environment &&
		JSON.stringify([...existing.game_versions].sort()) === JSON.stringify([...payload.game_versions].sort()) &&
		JSON.stringify([...existing.loaders].sort()) === JSON.stringify([...payload.loaders].sort()) &&
		JSON.stringify((existing.dependencies ?? []).map(dependencyKey).sort()) ===
			JSON.stringify(payload.dependencies.map(dependencyKey).sort()) &&
		existing.files.some((file) => file.filename === fileName)
	);
}

async function uploadVersion(payload, artifact, token) {
	const form = new FormData();
	form.append("data", JSON.stringify(payload));
	form.append("artifact", new Blob([await readFile(artifact)]), path.basename(artifact));
	return apiRequest(`${projectEndpoint}/version`, token, { method: "POST", body: form });
}

async function publish() {
	if (
		process.env.GITHUB_EVENT_NAME !== "push" ||
		process.env.GITHUB_REF_TYPE !== "tag" ||
		!process.env.GITHUB_REF_NAME
	) {
		throw new Error("Publishing is allowed only from a pushed version tag");
	}

	const { version, notes, targets } = await loadReleasePlan(false);
	const token = process.env.MDVSTOKEN;
	if (!token) {
		throw new Error("The MDVSTOKEN environment variable is required to publish");
	}

	const project = await apiRequest(`${projectEndpoint}/project/${projectSlug}`);
	if (project.slug !== projectSlug) {
		throw new Error(`Modrinth returned an unexpected project for ${projectSlug}`);
	}
	const existingVersions = await listProjectVersions(project.id, token);

	for (const target of targets) {
		const payload = createVersionPayload(version, target, notes, project.id);
		const existing = existingVersions.find((item) => item.version_number === target.versionNumber);
		if (existing) {
			if (!matchesExistingVersion(existing, payload, target.file)) {
				throw new Error(`Modrinth version ${target.versionNumber} already exists with different metadata`);
			}
			console.log(`Already published: ${target.versionNumber}`);
			continue;
		}

		const result = await uploadVersion(payload, path.join(root, "build", "libs", target.file), token);
		existingVersions.push(result);
		console.log(`Published ${target.versionNumber} (${target.file})`);
	}
}

async function publishDraftTest() {
	if (process.env.GITHUB_EVENT_NAME !== "workflow_dispatch" || !process.env.GITHUB_RUN_ID) {
		throw new Error("Draft test uploads are allowed only from a manual GitHub Actions run");
	}
	if (process.env.GITHUB_REF_NAME !== process.env.DEFAULT_BRANCH) {
		throw new Error("Draft test uploads must run from the default branch");
	}

	const version = await readVersion();
	const target = getReleaseTargets(version).find(
		(candidate) => candidate.minecraft === "1.21.11" && candidate.loader === "neoforge"
	);
	if (!target) {
		throw new Error("The NeoForge 1.21.11 test target is not configured");
	}

	const [changelog, token] = await Promise.all([
		readFile(path.join(root, "CHANGELOG.md"), "utf8").then((content) =>
			extractChangelog(content, version, true)
		),
		Promise.resolve(process.env.MDVSTOKEN)
	]);
	if (!token) {
		throw new Error("The MDVSTOKEN environment variable is required to create a draft test");
	}

	const artifact = path.join(root, "build", "libs", target.file);
	try {
		await stat(artifact);
	} catch {
		throw new Error(`Expected test jar is missing: ${path.relative(root, artifact)}`);
	}

	const project = await apiRequest(`${projectEndpoint}/project/${projectSlug}`);
	if (project.slug !== projectSlug) {
		throw new Error(`Modrinth returned an unexpected project for ${projectSlug}`);
	}
	const payload = createDraftTestPayload(version, target, changelog, project.id, process.env.GITHUB_RUN_ID);
	const existingVersions = await listProjectVersions(project.id, token);
	if (existingVersions.some((existing) => existing.version_number === payload.version_number)) {
		throw new Error(`Draft test version ${payload.version_number} already exists; refusing a duplicate upload`);
	}

	const result = await uploadVersion(payload, artifact, token);
	console.log(`Created Modrinth draft test: ${result.id}`);
	console.log(`Draft URL: https://modrinth.com/mod/${projectSlug}/version/${result.id}`);
	console.log(`Uploaded file: ${target.file}`);
}

async function main() {
	const mode = process.argv[2];
	if (mode !== "--check" && mode !== "--publish" && mode !== "--test-draft") {
		throw new Error("Usage: node scripts/publish-modrinth.mjs --check|--publish|--test-draft");
	}

	if (mode === "--check") {
		const { version, targets } = await loadReleasePlan(process.env.GITHUB_EVENT_NAME !== "push");
		console.log(`Validated ${targets.length} Modrinth entries for ${version}; no upload performed.`);
		return;
	}

	if (mode === "--publish") {
		await publish();
	} else {
		await publishDraftTest();
	}
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
	main().catch((error) => {
		console.error(error.message);
		process.exitCode = 1;
	});
}
