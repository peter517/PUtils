package com.pengjun.android.loadresource.cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcurrentLruMemoryCache<Key, Value> {
	private final ConcurrentHashMap<Key, Node> map = new ConcurrentHashMap<Key, Node>();
	private Node first = null;
	private Node last = null;
	private long maxCost;
	private long totalCost = 0;
	private OnRemovedListener onRemovedListener = null;

	public interface OnRemovedListener {
		void onRemoved(Object key, Object value);
	}

	public ConcurrentLruMemoryCache(long maxCost) {
		this.maxCost = maxCost;
	}

	public OnRemovedListener getOnRemovedListener() {
		return onRemovedListener;
	}

	public void setOnRemovedListener(OnRemovedListener listener) {
		onRemovedListener = listener;
	}

	synchronized public void clear() {
		if (onRemovedListener == null) {
			map.clear();

			first = null;
			last = null;
			totalCost = 0;
		} else {
			trim(0);
			// assert totalCost == 0 : "totalCost == 0";
		}
	}

	public boolean contains(Key key) {
		return map.containsKey(key);
	}

	public int count() {
		return size();
	}

	public int size() {
		return map.size();
	}

	synchronized public void updateCost(Key key, long cost) {
		Node node = map.get(key);
		if (node != null) {
			updateNode(node, node.value, cost);
		}
	}

	synchronized public boolean insert(Key key, Value object, long cost) {
		Node node = map.remove(key);
		if (node != null) {
			unlink(node);
		}

		return insertNew(key, object, cost);
	}

	synchronized public boolean replace(Key key, Value object, long cost) {
		Node node = map.get(key);
		if (node != null) {
			updateNode(node, object, cost);
			return true;
		} else {
			return insertNew(key, object, cost);
		}
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Key> keys() {
		return map.keySet();
	}

	public long getMaxCost() {
		return maxCost;
	}

	synchronized public void setMaxCost(long cost) {
		maxCost = cost;
		trim(maxCost);
	}

	synchronized public Value object(Key key) {
		return relink(key);
	}

	public boolean remove(Key key) {
		return take(key) != null;
	}

	synchronized public Value take(Key key) {
		Node node = map.remove(key);
		if (node != null) {
			unlink(node);
			return node.value;
		} else
			return null;
	}

	public long getTotalCost() {
		return totalCost;
	}

	public synchronized void reserved(long cost) {
		trim(maxCost - cost);
	}

	private boolean insertNew(Key key, Value object, long cost) {
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

	private void updateNode(Node node, Value value, long cost) {
		node.value = value;

		final long deltaCost = cost - node.cost;
		node.cost = cost;
		totalCost += deltaCost;
		trim(maxCost);
	}

	private class Node {
		public Key key;
		public Value value;
		public long cost;
		Node prev = null;
		Node next = null;

		public Node(Key key, Value value, long cost) {
			this.key = key;
			this.value = value;
			this.cost = cost;
		}
	}

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

		final OnRemovedListener tOnRemovedListener = onRemovedListener;
		if (tOnRemovedListener != null)
			tOnRemovedListener.onRemoved(node.key, node.value);

		node.value = null;// help gc
		node.key = null;// help gc
	}

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

	private void trim(long targetTotalCost) {
		Node n = last;
		while (n != null && totalCost > targetTotalCost) {
			Node u = n;
			n = n.prev;
			@SuppressWarnings("unused")
			Node removedNode = map.remove(u.key);
			// assert removedNode == u : "removedNode==u";
			unlink(u);
		}
	}
}
