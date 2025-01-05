package Helper;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

//Renderer cho nút trong JTable
public	class ButtonRenderer extends JPanel implements TableCellRenderer {
		public ButtonRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			JButton editButton = new JButton("Edit");
			JButton deleteButton = new JButton("Delete");
			add(editButton);
			add(deleteButton);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}