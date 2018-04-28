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

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    // This is the component that will handle the editing of the cell value
    JComponent component = new JTextField();

    // This method is called when a cell value is edited by the user.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)
        String cell = (String) value;
        String login = "";
        switch (vColIndex - 1) {
            case 0:
                login = Settings.STANDARD_LOGIN.getWebsite();
                break;
            case 1:
                login = Settings.STANDARD_LOGIN.getUsername();
                break;
            case 2:
                login = Settings.STANDARD_LOGIN.getPassword();
                break;
            default:
                login = Settings.STANDARD_LOGIN.getOther();
                break;
        }
        if (cell.equals(login)) {
            ((JTextField) component).setText("");
            ((JTextField) component).selectAll();
            return component;
        }
        ((JTextField) component).setText((String) value);
        ((JTextField) component).selectAll();
        return component;
    }
    
    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    @Override
    public Object getCellEditorValue() {
        ((JTextField) component).selectAll();
        return ((JTextField) component).getText();
    }
}
