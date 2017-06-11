/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Other;

import java.util.ArrayList;

/**
 *
 * @author Nikos
 */
public class HistoryNode {

    private HistoryNode nextNode;
    private HistoryNode previousNode;

    public ArrayList<Login> data;

    public HistoryNode(ArrayList<Login> data) {
        this.data = data;
    }

    public HistoryNode getNext() {
        return nextNode;
    }

    public void setNext(HistoryNode nextNode) {
        this.nextNode = nextNode;
    }

    public HistoryNode getPrevious() {
        return previousNode;
    }

    public void setPrevious(HistoryNode previousNode) {
        this.previousNode = previousNode;
    }

    public void display() {
        if (data != null) {
            System.out.println("---History Node---");
            for (Login l : data) {
                System.out.println(l.toString().replace("--!--", ""));
            }
            System.out.println("------------------");
        }
    }
}
