package com.pengjun.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ThreadExecutor {
	
	private static ThreadExecutor instace;
	public static ThreadExecutor getInstance(){
		if(instace == null){
			instace = new ThreadExecutor();
		}
		return instace;
	}
	 
	private static ExecutorService exec = Executors.newCachedThreadPool();
	private static CompletionService cs = new ExecutorCompletionService(exec); 
	
	public void execute(Runnable r){
		exec.execute(r);
	} 
	
	public Future submit(Runnable r){
		return exec.submit(r);
	} 
	
	public <T> T submitCallable(Callable<T> c) throws InterruptedException, ExecutionException{
		return exec.submit(c).get();
	}
	
	public <T> T submitCallable(Callable<T> c, Long waitSeconds) throws InterruptedException, ExecutionException, TimeoutException{
		return exec.submit(c).get(waitSeconds, TimeUnit.SECONDS);
	}
	
	public <T> T submitCallable(List<Callable<T>> cList) throws InterruptedException, ExecutionException{
		for(Callable<T> c:cList) {  
            cs.submit(c);  
        } 
		return (T) cs.take().get();
	}
	
	public void shutdown(){
		exec.shutdown();
	}
	
	
	
	public static void main(String []args){
		
		 class A{
			public int a = 1;
			public String b = "234";
			@Override
			public int hashCode() {
				return new HashCodeBuilder(17, 37).
						append(a).
						append(b).
						toHashCode();
			}
			@Override
			public boolean equals(Object obj) {
				if(obj instanceof A){
					A tmp = (A) obj;
					if(a == tmp.a && b.equals(tmp.b)){
						return true;
					}
				}
				return false;
			}
			
		}
		 
		 A a1 = new A();
		 A a2 = new A();
		 Map map = new HashMap();
		 map.put(a1, "123");
		 map.put(a2, "321");
		 map.put("123", "321");
		 map.put(1, "321");
		 
		 System.out.println(ArrayUtils.toString(map));
	}
}
