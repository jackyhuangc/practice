package com.jacky.practice;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

//import net.sf.cglib.proxy.Enhancer;
//import net.sf.cglib.proxy.MethodInterceptor;
//import net.sf.cglib.proxy.MethodProxy;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-04 16:53
 * @since jdk1.8
 */
public class T_Proxy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JDKProxy jdkProxy = new JDKProxy();

		// 相当于接口
		UserManager ujdk = (UserManager) jdkProxy.newProxy(new UserManagerImpl());
		ujdk.addUser("123", "12");

		// 相当于继承
		UserManager ucglib = (UserManager) new CGLibProxy().createProxyObject(new UserManagerImpl());
		ucglib.addUser("111", "11");

		UserManager u = new UserManager() {

			@Override
			public void addUser(String id, String password) {
				// TODO Auto-generated method stub
				System.out.println("xxxxxxxxxxxx" + id + password);
			}

			@Override
			public void delUser(String id) {
				// TODO Auto-generated method stub

			}

		};

		u.addUser("xxx", "bbb");
	}
}

class TimerFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		Servlet x;
		HttpServletRequest x1;
		HttpServlet h1;

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}

// HttpServlet类实现Servlet接口
class HelloServlet extends HttpServlet {

	// HTTP协议中的请求和响应就是对应了 HttpServletRequest 和 HttpServletResponse 这两个接口
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print("Hello World");
	}
}

// Servlet 规范里还有另外一个非常重要而且非常有用的接口那就是 Filter 过滤器。
class HelloFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("Filter init...");
	}

	// Filter不是一个servlet，它不能产生一个response，但是他能在request到达servlet之前预先处理request，也可以在响应离开servlet时处理response。
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		System.out.println("拦截 URI=" + request.getRequestURI());
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		System.out.println("Filter destroy..");
	}
}

interface UserManager {
	void addUser(String id, String password);

	@Autowired()
	void delUser(String id);
}

class UserManagerImpl implements UserManager {

	@Override
	public void addUser(String id, String password) {
		// TODO Auto-generated method stub
		System.out.println("调用了addUser" + id + "***" + password);
	}

	@Override
	public void delUser(String id) {
		// TODO Auto-generated method stub
		System.out.println("调用了addUser" + id);
	}
}

// JDK代理
class JDKProxy implements InvocationHandler {
	private Object targetObject;

	public Object newProxy(Object targetObject) {
		// TODO Auto-generated method stub
		this.targetObject = targetObject;

		return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(),
				this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object ret = null;

		// 添加切面逻辑（advice），此处是在目标类代码执行之前
		ret = method.invoke(targetObject, args);
		// 添加切面逻辑（advice），此处是在目标类代码执行之后
		return ret;
	}
}

class CGLibProxy implements MethodInterceptor {
	Object targetObject;

	public Object createProxyObject(Object obj) {
		this.targetObject = obj;
		Enhancer enhaner = new Enhancer();
		enhaner.setSuperclass(obj.getClass());
		enhaner.setCallback(this);

		Object proxyObj = enhaner.create();
		return proxyObj;
	}

	@Override
	public Object intercept(Object targetObject, Method method, Object[] args, MethodProxy arg3) throws Throwable {

		// 添加切面逻辑（advice），此处是在目标类代码执行之前
		Object ret = method.invoke(this.targetObject, args);
		// 添加切面逻辑（advice），此处是在目标类代码执行之后
		return ret;
	}
}