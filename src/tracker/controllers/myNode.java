package tracker.controllers;

public class myNode<E> {

    public myNode<E> previous;
    public E data;
    public myNode<E> next;

    public myNode(myNode<E> previous, E data, myNode<E> next) {
        this.previous = previous;
        this.data = data;
        this.next = next;
    }

    public myNode<E> getPrevious() {
        return previous;
    }

    public void setPrevious(myNode<E> previous) {
        this.previous = previous;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public myNode<E> getNext() {
        return next;
    }

    public void setNext(myNode<E> next) {
        this.next = next;
    }
}
