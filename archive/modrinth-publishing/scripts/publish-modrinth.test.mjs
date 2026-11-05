import assert from "node:assert/strict";
import { spawnSync } from "node:child_process";
import test from "node:test";
import { fileURLToPath } from "node:url";

import {
	createDraftTestPayload,
	createVersionPayload,
	extractChangelog,
	getReleaseTargets
} from "./publish-modrinth.mjs";

const changelog = `# Changelog

## 1.4-r2 - 05/11/2026

- Improved telemetry efficiency.
- Fixed settings input parity.

## 1.4-r1 - 31/10/2026

- Added Minecraft targets.
`;

test("extracts release notes without the changelog heading", () => {
	assert.equal(
		extractChangelog(changelog, "1.4-r2"),
		"- Improved telemetry efficiency.\n- Fixed settings input parity."
	);
});

test("refuses to publish a development changelog section", () => {
	assert.throws(
		() => extractChangelog("## 1.4-r2 (in development)\n\n- Work in progress.", "1.4-r2"),
		/marks 1\.4-r2 as in development/
	);
});

test("creates one precise entry for each supported loader and Minecraft target", () => {
	const targets = getReleaseTargets("1.4-r2");

	assert.equal(targets.length, 10);
	assert.equal(new Set(targets.map((target) => target.file)).size, 10);
	assert.equal(new Set(targets.map((target) => target.versionNumber)).size, 10);
	assert.deepEqual(
		targets.find((target) => target.minecraft === "26.1.x" && target.loader === "fabric").gameVersions,
		["26.1", "26.1.1", "26.1.2"]
	);

	const legacyFabric = targets.find((target) => target.minecraft === "1.21.1" && target.loader === "fabric");
	const newerLegacyFabric = targets.find((target) => target.minecraft === "1.21.11" && target.loader === "fabric");
	assert.deepEqual(
		legacyFabric.dependencies.filter((dependency) => dependency.dependency_type === "required"),
		[{ project_id: "P7dR8mSH", dependency_type: "required" }]
	);
	assert.deepEqual(
		newerLegacyFabric.dependencies.filter((dependency) => dependency.dependency_type === "required"),
		[{ project_id: "P7dR8mSH", dependency_type: "required" }]
	);

	const currentFabric = targets.find((target) => target.minecraft === "26.3" && target.loader === "fabric");
	assert.equal(currentFabric.dependencies.some((dependency) => dependency.dependency_type === "required"), false);

	const affectedNeoForge = targets.find((target) => target.minecraft === "1.21.11" && target.loader === "neoforge");
	assert.deepEqual(
		affectedNeoForge.dependencies
			.filter((dependency) => dependency.dependency_type === "incompatible")
			.map((dependency) => dependency.version_id)
			.sort(),
		["sC6wj32u", "sVLtC9EW"]
	);
});

test("builds a client-only version payload with target-specific compatibility", () => {
	const target = getReleaseTargets("1.4-r2").find(
		(entry) => entry.minecraft === "1.21.11" && entry.loader === "fabric"
	);
	const payload = createVersionPayload("1.4-r2", target, "- Release notes.", "AQGahqU5");

	assert.equal(payload.version_number, "1.4-r2-fabric-mc1-21-11");
	assert.deepEqual(payload.game_versions, ["1.21.11"]);
	assert.deepEqual(payload.loaders, ["fabric"]);
	assert.equal(payload.environment, "client_only");
	assert.deepEqual(payload.file_parts, ["artifact"]);
	assert.equal(payload.changelog, "- Release notes.");
});

test("builds a clearly labeled draft test version with real target metadata", () => {
	const target = getReleaseTargets("1.4-r2").find(
		(entry) => entry.minecraft === "1.21.11" && entry.loader === "neoforge"
	);
	const payload = createDraftTestPayload("1.4-r2", target, "- Test notes.", "AQGahqU5", "123456");

	assert.equal(payload.name, "TEST ONLY - AccurateFPS+ 1.4-r2 - Minecraft 1.21.11 (NeoForge)");
	assert.equal(payload.version_number, "1.4-r2-test-123456");
	assert.equal(payload.status, "draft");
	assert.match(payload.changelog, /TEST ONLY: draft metadata validation/);
	assert.match(payload.changelog, /- Test notes\./);
	assert.equal(payload.dependencies.filter((dependency) => dependency.dependency_type === "optional").length, 3);
	assert.equal(payload.dependencies.filter((dependency) => dependency.dependency_type === "incompatible").length, 2);
	assert.throws(
		() => createDraftTestPayload("1.4-r2", target, "- Test notes.", "AQGahqU5", "../bad"),
		/numeric GitHub Actions run ID/
	);
});

test("refuses publishing outside a pushed Git tag", () => {
	const script = fileURLToPath(new URL("./publish-modrinth.mjs", import.meta.url));
	const result = spawnSync(process.execPath, [script, "--publish"], {
		encoding: "utf8",
		env: {
			...process.env,
			GITHUB_EVENT_NAME: "",
			GITHUB_REF_TYPE: "",
			GITHUB_REF_NAME: "",
			MDVSTOKEN: ""
		}
	});

	assert.equal(result.status, 1);
	assert.match(result.stderr, /Publishing is allowed only from a pushed version tag/);
});

test("refuses draft uploads outside a manual workflow run", () => {
	const script = fileURLToPath(new URL("./publish-modrinth.mjs", import.meta.url));
	const result = spawnSync(process.execPath, [script, "--test-draft"], {
		encoding: "utf8",
		env: {
			...process.env,
			GITHUB_EVENT_NAME: "",
			GITHUB_RUN_ID: "",
			GITHUB_REF_NAME: "",
			DEFAULT_BRANCH: "",
			MDVSTOKEN: ""
		}
	});

	assert.equal(result.status, 1);
	assert.match(result.stderr, /Draft test uploads are allowed only from a manual GitHub Actions run/);
});
