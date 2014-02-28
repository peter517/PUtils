package com.pengjun.thread;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadExecutor {

	private static ThreadExecutor instace;

	public static ThreadExecutor getInstance() {
		if (instace == null) {
			instace = new ThreadExecutor();
		}
		return instace;
	}

	private static ExecutorService exec = Executors.newCachedThreadPool();
	private static CompletionService cs = new ExecutorCompletionService(exec);

	public void execute(Runnable r) {
		exec.execute(r);
	}

	public Future submit(Runnable r) {
		return exec.submit(r);
	}

	public <T> T submitCallable(Callable<T> c) throws InterruptedException,
			ExecutionException {
		return exec.submit(c).get();
	}

	public <T> T submitCallable(Callable<T> c, Long waitSeconds)
			throws InterruptedException, ExecutionException, TimeoutException {
		return exec.submit(c).get(waitSeconds, TimeUnit.SECONDS);
	}

	// for a list of tasks to be submitted
	public <T> T submitCallable(List<Callable<T>> cList)
			throws InterruptedException, ExecutionException {
		for (Callable<T> c : cList) {
			cs.submit(c);
		}
		return (T) cs.take().get();
	}

	public void shutdown() {
		exec.shutdown();
	}

	public int c = 1;

	public int getC() {
		return c;
	}

	public void setA(int c) {
		this.c = c;
	}

	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
		Future<Integer> future = threadPool.submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				return new Random().nextInt(100);
			}
		});
		try {
			Thread.sleep(5000);// 可能做一些事情
			System.out.println(future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
