package com.pamplona.turnos.adaptadores.entrada.swing.vista;

import com.pamplona.turnos.dominio.Persona;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.util.List;

/** Utilidades compartidas por las vistas. */
final class Componentes {

    private Componentes() { }

    static JTable tabla(String... columnas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        return new JTable(modelo);
    }

    static void llenar(JTable tabla, List<Object[]> filas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    /** Recarga un combo conservando la seleccion previa; opcionalmente agrega "(Todos)" como null. */
    static <T> void recargar(JComboBox<T> combo, List<T> items, boolean conTodos) {
        Object anterior = combo.getSelectedItem();
        combo.removeAllItems();
        if (conTodos) {
            combo.addItem(null);
        }
        for (T item : items) {
            combo.addItem(item);
        }
        if (anterior != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                T actual = combo.getItemAt(i);
                if (actual != null && actual.toString().equals(anterior.toString())) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    /** Muestra cada persona como "[id] Nombre (doc 123)" y el valor null como "(Todos)". */
    static void rendererPersonas(JComboBox<?> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value == null) {
                    l.setText("(Todos)");
                } else if (value instanceof Persona p) {
                    l.setText("[" + p.getId() + "] " + p.getNombreCompleto() + " (doc " + p.getDocumento() + ")");
                }
                return l;
            }
        });
    }

    static EmptyBorder margen() {
        return new EmptyBorder(10, 10, 10, 10);
    }
}
