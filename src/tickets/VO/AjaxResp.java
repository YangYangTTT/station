package tickets.VO;

public class AjaxResp {
    private int code;
    private String msg;
    private Object result;

    public static AjaxResp ok(Object object) {
        return new AjaxResp(object);
    }

    public static AjaxResp error(int code, String msg) {
        return new AjaxResp(code, msg);
    }

    public AjaxResp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResp(Object result) {
        this.result = result;
    }

    public AjaxResp() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
