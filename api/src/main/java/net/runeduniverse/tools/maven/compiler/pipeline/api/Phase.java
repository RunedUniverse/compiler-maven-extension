package net.runeduniverse.tools.maven.compiler.pipeline.api;

public enum Phase {

	PRE_PREPROCESSOR("pre-preprocessor"), //
	PREPROCESSOR("preprocessor"), //
	POST_PREPROCESSOR("post-preprocessor"), //
	PRE_COMPILER("pre-compiler"), //
	COMPILER("compiler"), //
	POST_COMPILER("post-compiler"), //
	PRE_ASSEMBLER("pre-assembler"), //
	ASSEMBLER("assembler"), //
	POST_ASSEMBLER("post-assembler"), //
	PRE_LINKER("pre-linker"), //
	LINKER("linker"), //
	POST_LINKER("post-linker"), //
	PRE_PACK_BINARY("pre-pack-binary"), //
	PACK_BINARY("pack-binary"), //
	POST_PACK_BINARY("post-pack-binary");

	private final String id;

	private Phase(final String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

}
