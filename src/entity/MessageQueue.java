package entity;

import java.io.Serializable;

import update.MessageUpdate;

public final class MessageQueue implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final int capacity;
	private int size;
	private MessageNode head;
	private MessageNode tail;
	
	public MessageQueue(int capacity) throws IllegalArgumentException
	{
		if(capacity < 1)
		{
			throw new IllegalArgumentException("Capacity cannot be less than 1");
		}
		
		this.capacity = capacity;
	}
	
	private static class MessageNode implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private MessageUpdate message;
		private MessageNode next;
		
		public MessageNode(MessageUpdate message, MessageNode next)
		{
			this.message = message;
			this.next = next;
		}
		
		public void setNext(MessageNode next)
		{
			this.next = next;
		}
		
		public MessageNode getNext()
		{
			return next;
		}

		public MessageUpdate getMessage()
		{
			return message;
		}
	}
	
	public synchronized boolean isEmpty()
	{
		return(size == 0);
	}
	
	public synchronized boolean isFull()
	{
		return(size == capacity);
	}
	
	public synchronized boolean putMessage(MessageUpdate message)
	{
		if(!isFull())
		{
			MessageNode newNode = new MessageNode(message, null);
			
			if(isEmpty())
			{
				head = newNode;
				tail = newNode;
			}
			else
			{
				tail.setNext(newNode);
				tail = newNode;
			}
			
			size++;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized MessageUpdate takeMessage()
	{
		if(!isEmpty())
		{
			MessageNode current = head;
			head = head.getNext();
			
			if(head == null)
			{
				tail = null;
			}
			
			size--;
			
			return current.getMessage();
		}
		else
		{
			return null;
		}
	}
	
	//Not synchronized since it is declared to be final
	public int getCapcity()
	{
		return capacity;
	}
	
	public synchronized int getSize()
	{
		return size;
	}
	
	public synchronized int getRemainingCapacity()
	{
		return capacity - size;
	}
}
