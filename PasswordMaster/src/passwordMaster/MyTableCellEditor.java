/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordMaster;

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
        System.out.println(value);
        switch (vColIndex - 1) {
            case 0:
                login = Settings.standardLogin.getWebsite();
                break;
            case 1:
                login = Settings.standardLogin.getUsername();
                break;
            case 2:
                login = Settings.standardLogin.getPassword();
                break;
            default:
                login = Settings.standardLogin.getOther();
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
        System.out.println(((JTextField) component).getText());
        ((JTextField) component).selectAll();
        return ((JTextField) component).getText();
    }
}
