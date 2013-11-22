package com.pengjun.android.loadresource;

public final class CircularStack<T> {
	private int capacity;
	private T[] stack;
	private int top = 0;

	/**
     * 
     */
	@SuppressWarnings("unchecked")
	public CircularStack(int capacity) {
		assert capacity > 0 : "capacity>0";
		this.capacity = capacity + 1;
		this.stack = (T[]) new Object[this.capacity];
	}

	/**
     * 
     */
	@SuppressWarnings("unchecked")
	public void resize(int newcapacity) {
		assert newcapacity > 0 : "newcapacity>0";

		++newcapacity;
		if (newcapacity == this.capacity)
			return;

		T[] newqueue = (T[]) new Object[newcapacity];

		final int lastIndex = indexOfLast();
		int oldIter = lastIndex;
		int newIter = newcapacity - 1;

		do {
			if (stack[oldIter] == null)
				break;

			newqueue[newIter] = stack[oldIter];

			--newIter;
			if (newIter < 0)
				break;

			oldIter = (oldIter > 0 ? (oldIter - 1) : capacity - 1);
		} while (oldIter != lastIndex);

		this.capacity = newcapacity;
		this.stack = newqueue;
	}

	/**
     * 
     */
	public T get(int i) {
		assert i < 0 || i >= getCapacity() : "Index " + i
				+ ", stack array size " + getCapacity();
		return stack[i];
	}

	/**
     * 
     */
	public int getCapacity() {
		return capacity;
	}

	/**
     * 
     */
	public T push(T item) {
		T discardItem = stack[top];
		stack[top] = item;
		top = (top + 1) % capacity;
		return discardItem;
	}

	/**
     * 
     */
	public T pop() {
		top = indexOfLast();
		T obj = stack[top];
		stack[top] = null;
		return obj;
	}

	/**
     * 
     */
	public T peek() {
		int last = indexOfLast();
		return stack[last];
	}

	/**
     * 
     */
	public int size() {

		final int lastIndex = indexOfLast();
		int i = lastIndex;

		int count = 0;
		do {
			if (stack[i] != null)
				++count;
			else
				break;
			i = (i > 0 ? (i - 1) : capacity - 1);
		} while (i != lastIndex);

		return count;
	}

	/**
     * 
     */
	public boolean empty() {
		final int lastIndex = indexOfLast();

		if (stack[lastIndex] != null)
			return false;
		else
			return true;
	}

	/**
     * 
     */
	public int search(Object o) {
		final int lastIndex = indexOfLast();
		int i = lastIndex;
		do {
			if (stack[i] == null)
				break;
			if (stack[i] == o)
				return i;
			i = (i > 0 ? (i - 1) : capacity - 1);
		} while (i != lastIndex);

		return -1;
	}

	/**
     * 
     */
	public int indexOfLast() {
		return top > 0 ? (top - 1) : capacity - 1;
	}
}
