
package com.fxg.archetype.api;

import com.fxg.archetype.exception.ExceptionDescriptor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;


/**
 * <p> 接口应答对象,包括code:应答码,desc:应答描述,data:数据域 </p>
 */
@ApiModel(value = "http响应对象")
public class HttpResult<T> {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@ApiModelProperty(value = "响应状态码：200-成功 400-客户端参数错误 401-未登陆 403-鉴权失败 404-资源不存在 500-系统内部错 502-未知错误")
	private Integer code;
	@ApiModelProperty(value = "响应描述")
	private String desc;
	@ApiModelProperty(value = "响应数据")
	private T data;


    private List<ExceptionDescriptor> exceptionDescriptors;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public List<ExceptionDescriptor> getExceptionDescriptors() {
        return exceptionDescriptors;
    }

    public void setExceptionDescriptors(List<ExceptionDescriptor> exceptionDescriptors) {
        this.exceptionDescriptors = exceptionDescriptors;
    }

    public HttpResult() {
        this(null, null, null, null);
    }


    public HttpResult(Integer code, String desc) {

        this(code, desc, null, null);
    }

    public HttpResult(Integer code, String desc, T data) {

        this(code, desc, data, null);
    }

    public HttpResult(Integer code, String desc, T data, List<ExceptionDescriptor> exceptionDescriptors) {
        this.code = code;
        this.desc = desc;
        this.data = data;
        this.exceptionDescriptors = exceptionDescriptors;

    }

    @Override
    public String toString() {
        return "HttpResult [code=" + code + ", desc=" + desc + ", data=" + data + "]";
    }

	/**
	 * 校验接口返回状态，需自定义错误处理逻辑
	 * @param handler
	 * @return
	 */
	public T checkResult(Function<HttpResult<T>, T> handler) {
        if (this.code.equals(HttpStatus.OK)) {
            //正常返回结果了
            return this.data;
        } else {
            return Objects.isNull(handler) ? null : handler.apply(this);
        }
    }

	/**
	 * 校验接口返回状态,不是 200 的话打个 error 日志,不往外抛异常
	 * @return
	 */
	public T checkResultSilent() {
        return this.checkResult(r -> {
                    logger.error("远程服务调用返业务错误 {} {}", r.code, r.desc);
                    return null;
                }
        );
    }

    public static <X> HttpResult<X> ok(X data) {
        return new HttpResult<>(HttpStatus.OK, "ok", data);
    }


}
