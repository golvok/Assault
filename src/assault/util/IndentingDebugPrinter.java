package assault.util;


public class IndentingDebugPrinter {

	public static class LineTag {
		public String value;
		public boolean enabled = true;
		public IndentLevel indentLevel;
		public PrefixTag prefixTag;
		
		public LineTag(String value, PrefixTag prefixTag, IndentLevel indentLevel){
			this(value, prefixTag, true, indentLevel);
		}
		
		public LineTag(String value, PrefixTag prefixTag, boolean enabled, IndentLevel indentLevel) {
			this.value = value;
			this.prefixTag = prefixTag;
			this.enabled = enabled;
			this.indentLevel = indentLevel;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static class IndentLevel {
		public int nIndents = 0;
		public void increment(){
			nIndents++;
		}
		public void decrement(){
			nIndents--;
		}
	}
	
	public static class PrefixTag {
		public String value;
		public PrefixTag(String value){
			this.value = value;
		}
		@Override
		public String toString() {
			return value;
		}
	}
	
	/**
	 * for optimisation. ie prevent implicitly creating string buffers and the like unless actually printing.
	 */
	public static abstract class Line{
		public abstract String l();
		@Override
		public String toString() {
			return l();
		}
	}
	
	public static void dp(final String line, LineTag... lineTags) {
		dp(new Line() {
			@Override
			public String l() {
				return line;
			}
		},lineTags);
	}

	/**
	 * optimised version. The string should never be constructed unless it actually prints
	 */
	public static void dp(Line line, LineTag... lineTags) {
		LineTag firstEnabledLineTag = getFirstEnabledLineTag(lineTags);
		if(firstEnabledLineTag != null){
			String prefix = firstEnabledLineTag.prefixTag + ": " + repeatchar(firstEnabledLineTag.indentLevel.nIndents * 2, ' ');
			System.out.println(prefix + line.toString().replace("\n", '\n' + prefix));
		}
	}
	
	public static LineTag getFirstEnabledLineTag(LineTag... lineTags){
		for(LineTag tag: lineTags){
			if (tag.enabled == true){
				return tag;
			}
		}
		return null;
	}
	
	public static Class<?> getCallingClass(int extralevels){
		try {
			return Class.forName(new Exception().getStackTrace()[2 + extralevels].getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String repeatchar(int n, char c){
		String out = "";
		for (int i = 0; i < n; i++) {
			out += c;
		}
		return out;
	}
	

}
