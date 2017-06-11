package Other;

import java.util.ArrayList;

public class History {

    HistoryNode head;
    HistoryNode tail;
    public int nItems;
    public int changeCounter;

    /**
     *
     * @param maxSize
     */
    public History() {
        head = new HistoryNode(null);
        tail = new HistoryNode(null);
        head.setNext(tail);
        tail.setPrevious(head);
        nItems = 0;
        changeCounter = 0;
    }

    private boolean isEmpty() {
        return nItems == 0;
    }

    public int getSize() {
        return nItems;
    }

    /**
     *
     * @param rankedTweet
     * @return
     */
    public void insert(ArrayList<Login> list) {
        insertNodeAfter(list, getChange());
        nItems = ++changeCounter;
    }

    private void insertNodeAfter(ArrayList<Login> list, HistoryNode node) {
        ArrayList<Login> nodeData = new ArrayList();
        for(Login l:list){
            nodeData.add(Login.fromString(l.toString()));
        }
        if(!isEmpty()){
            HistoryNode newNode = new HistoryNode(nodeData);
            newNode.setNext(tail);
            newNode.setPrevious(node);
            node.setNext(newNode);
            tail.setPrevious(newNode);
        } else {
            HistoryNode newNode = new HistoryNode(nodeData);
            head.setNext(newNode);
            tail.setPrevious(newNode);
            newNode.setNext(tail);
            newNode.setPrevious(head);
        }
    }

    private boolean undo = false;
    public ArrayList<Login> undo() {
        changeCounter--;
        if (changeCounter < 0) {
            changeCounter = 0;
        }
        undo = true;
        redo = false;
        return getChange().data;
    }
    
    private boolean redo = false;
    public ArrayList<Login> redo() {
        changeCounter++;
        if (changeCounter > nItems) {
            changeCounter = nItems;
        }
        undo = false;
        redo = true;
        return getChange().data;
    }

    public HistoryNode getChange() {
        HistoryNode temp = tail.getPrevious();
        for (int i = 0; i < nItems - changeCounter; i++) {
            temp = temp.getPrevious();
        }
        if(temp == head){
            if(isEmpty()){
                return null;
            } else {
                if(undo && !redo){
                    changeCounter++;
                    undo = false;
                } else if(redo && !undo){
                    changeCounter--;
                    redo = false;
                }
                return head.getNext();
            }
        }
        return temp;
    }

//    public void display() {
//        HistoryNode temp = head.getNext();
//        int i = 0;
//        while (temp != tail) {
//            System.out.println(i++);
//            temp.display();
//            temp = temp.getNext();
//        }
//    }

    public void displayCurrent() {
        System.out.println("Current Node:" + changeCounter);
        getChange().display();
    }
}
