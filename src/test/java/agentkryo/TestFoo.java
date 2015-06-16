package agentkryo;

public class TestFoo {

	private static final TestBar[] TEST_BARS = new TestBar[] {new TestBar("walks", "into"), new TestBar("leaves", "broke"), new TestBar("goes", "home")};
	int numberOfClass = 0;
	
	public TestBar[] getBars(){
		return TEST_BARS;
	}

	public Integer getNumberOfCalls() {
		return ++numberOfClass;
	}

	public String getBar(int i) {
		return TEST_BARS[i].getBar();
	}

	public String getFoo(int i) {
		return TEST_BARS[i].getFoo();
	}
}
