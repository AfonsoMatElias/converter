package io.github.afonsomatelias.Options.MemberMapping;

@SuppressWarnings("unchecked")
public class MemberObjectMapping {
	private final MemberMapping memberConverter;

	public MemberObjectMapping(MemberMapping memberConverter) {
		this.memberConverter = memberConverter;
	}

	public <D> D to(Class<D> clazz) {
		try {
			return (D) this.memberConverter.processor.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}