/*
 * 	https://github.com/nikoskalai/Password-Master
 *
 * 	Copyright (c) 2018 Nikos Kalaitzian
 * 	Licensed under the WTFPL
 * 	You may obtain a copy of the License at
 *
 * 	http://www.wtfpl.net/about/
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
