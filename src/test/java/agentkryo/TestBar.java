package agentkryo;

public class TestBar {

	private String foo;
	private String bar;

	public TestBar(String bar, String foo) {
		this.bar = bar;
		this.foo = foo;
	}

	public String getBar() {
		return bar;
	}

	public String getFoo() {
		return foo;
	}
	
	@Override
	public String toString() {
		return "TestBar [foo=" + foo + ", bar=" + bar + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bar == null) ? 0 : bar.hashCode());
		result = prime * result + ((foo == null) ? 0 : foo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestBar other = (TestBar) obj;
		if (bar == null) {
			if (other.bar != null)
				return false;
		} else if (!bar.equals(other.bar))
			return false;
		if (foo == null) {
			if (other.foo != null)
				return false;
		} else if (!foo.equals(other.foo))
			return false;
		return true;
	}

	

}
