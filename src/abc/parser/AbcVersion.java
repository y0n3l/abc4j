/**
 * 
 */
package abc.parser;

public class AbcVersion {
	
	public static final AbcVersion v1_6 = new AbcVersion(1.6f);
	public static final AbcVersion v2_0 = new AbcVersion(2f);
	
	private float m_version = 0f;
	
	private AbcVersion(float version) {
		m_version = version;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AbcVersion) {
			return ((AbcVersion) o).m_version == m_version;
		} else
			return super.equals(o);
	}

}
