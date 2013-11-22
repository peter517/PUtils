package com.pengjun.android.loadresource.cache;

import java.util.HashMap;
import java.util.Set;

public final class LruMemoryCache<Key, Value> {
	private HashMap<Key, Node> map = new HashMap<Key, Node>();

	private Node first = null;
	private Node last = null;
	private int maxCost;
	private int totalCost = 0;
	private OnRemovedListener onRemovedListener = null;

	/**
     * 
     */
	public interface OnRemovedListener {
		void onRemoved(Object key, Object value);
	}

	/**
     * 
     */
	public LruMemoryCache(int maxCost) {
		this.maxCost = maxCost;
	}

	/**
     * 
     */
	public OnRemovedListener getOnRemoveListener() {
		return onRemovedListener;
	}

	/**
     * 
     */
	public void setOnRemoveListener(OnRemovedListener listener) {
		onRemovedListener = listener;
	}

	/**
     * 
     */
	public void clear() {
		if (onRemovedListener == null) {
			map.clear();

			first = null;
			last = null;
			totalCost = 0;
		} else {
			trim(0);
			assert totalCost == 0 : "totalCost == 0";
		}
	}

	/**
     * 
     */
	public boolean contains(Key key) {
		return map.containsKey(key);
	}

	/**
     * 
     */
	public int count() {
		return size();
	}

	/**
     * 
     */
	public int size() {
		return map.size();
	}

	/**
     * 
     */
	public boolean insert(Key key, Value object, int cost) {
		remove(key);
		if (cost > maxCost)
			return false;

		trim(maxCost - cost);

		Node newNode = new Node(key, object, cost);
		map.put(key, newNode);
		totalCost += cost;

		if (first != null)
			first.prev = newNode;
		newNode.next = first;
		first = newNode;
		if (last == null)
			last = first;

		return true;
	}

	/**
     * 
     */
	public boolean isEmpty() {

		return map.isEmpty();
	}

	/**
     * 
     */
	public Set<Key> keys() {
		return map.keySet();
	}

	/**
     * 
     */
	public int getMaxCost() {
		return maxCost;
	}

	/**
     * 
     */
	public void setMaxCost(int cost) {
		maxCost = cost;
		trim(maxCost);
	}

	/**
     * 
     */
	public Value object(Key key) {
		return relink(key);
	}

	/**
     * 
     */
	public boolean remove(Key key) {
		return take(key) != null;
	}

	/**
     * 
     */
	public Value take(Key key) {
		Node node = map.remove(key);
		if (node != null) {
			unlink(node);
			return node.value;
		} else
			return null;
	}

	/**
     * 
     */
	public int getTotalCost() {
		return totalCost;
	}

	/**
     * 
     */
	private class Node {
		public Key key;
		public Value value;
		public int cost;
		Node prev = null;
		Node next = null;

		/**
         * 
         */
		public Node(Key key, Value value, int cost) {
			this.key = key;
			this.value = value;
			this.cost = cost;
		}
	}

	/**
     * 
     */
	private void unlink(Node node) {
		if (node.prev != null)
			node.prev.next = node.next;
		if (node.next != null)
			node.next.prev = node.prev;
		if (last == node)
			last = node.prev;
		if (first == node)
			first = node.next;
		totalCost -= node.cost;

		if (onRemovedListener != null)
			onRemovedListener.onRemoved(node.key, node.value);
	}

	/**
     * 
     */
	private Value relink(Key key) {
		Node node = map.get(key);
		if (node == null)
			return null;

		if (first != node) {
			if (node.prev != null)
				node.prev.next = node.next;
			if (node.next != null)
				node.next.prev = node.prev;
			if (last == node)
				last = node.prev;
			node.prev = null;
			node.next = first;
			first.prev = node;
			first = node;
		}

		return node.value;
	}

	/**
     * 
     */
	private void trim(int targetTotalCost) {
		Node n = last;
		while (n != null && totalCost > targetTotalCost) {
			Node u = n;
			n = n.prev;
			Node removedNode = map.remove(u.key);
			assert removedNode == u : "removedNode==u";
			unlink(u);
		}
	}
}
