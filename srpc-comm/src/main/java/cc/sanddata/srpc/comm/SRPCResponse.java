package cc.sanddata.srpc.comm;

public class SRPCResponse {
	private String requestId;
	
	private Throwable error;
	
	private Object result;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	/**
	 * 
	 * @Title: isError 
	 * @author wanggl
	 * @Description: 调用是否有异常
	 * @param @return 
	 * @return boolean
	 * @throws
	 */
	public boolean isError(){
		return error!=null;
	}
}
