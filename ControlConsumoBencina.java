import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

public class ControlConsumoBencina extends JFrame {

    // Campos de entrada
    private JTextField campoPatente;
    private JTextField campoKilometros;
    private JTextField campoLitros;

    // Área para mostrar resultados
    private JTextArea areaTexto;

    // Botones
    private JButton btnRegistrar;
    private JButton btnMostrar;
    private JButton btnEstadisticas;
    private JButton btnFiltrar;

    // Listas para guardar datos
    private ArrayList<String> listaPatentes;
    private ArrayList<Double> listaKilometros;
    private ArrayList<Double> listaLitros;
    private ArrayList<Double> listaRendimientos;

    //constructor
    public ControlConsumoBencina() {

        // Inicializar listas
        listaPatentes = new ArrayList<>();
        listaKilometros = new ArrayList<>();
        listaLitros = new ArrayList<>();
        listaRendimientos = new ArrayList<>();

        // Configuración de la ventana
        setTitle("Control de Consumo de Bencina");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        // Panel de campos
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(2, 3, 10, 10));

        panelCampos.add(new JLabel("Patente"));
        panelCampos.add(new JLabel("Kilómetros recorridos"));
        panelCampos.add(new JLabel("Litros cargados"));

        campoPatente = new JTextField();
        campoKilometros = new JTextField();
        campoLitros = new JTextField();

        panelCampos.add(campoPatente);
        panelCampos.add(campoKilometros);
        panelCampos.add(campoLitros);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());

        btnRegistrar = new JButton("Registrar Consumo");
        btnMostrar = new JButton("Mostrar Registros");
        btnEstadisticas = new JButton("Calcular Estadísticas");
        btnFiltrar = new JButton("Filtrar Bajo Rendimiento");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnMostrar);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnFiltrar);

        // addActionListener, dentro del constructor
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarConsumo();
            }
        });

        btnMostrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarRegistros();
            }
        });

        btnEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calcularEstadisticas();
            }
        });

        btnFiltrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filtrarBajoRendimiento();
            }
        });

        // Área de texto
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);

        JScrollPane scroll = new JScrollPane(areaTexto);

        //Armar la ventana
        panelPrincipal.add(panelCampos, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);

        setVisible(true); // muestra la ventana
    }

    public void registrarConsumo() {

        String patente = campoPatente.getText().trim();
        String kilometrosTexto = campoKilometros.getText().trim();
        String litrosTexto = campoLitros.getText().trim();

        if (patente.isEmpty() || kilometrosTexto.isEmpty() || litrosTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        try {
            double kilometros = Double.parseDouble(kilometrosTexto);
            double litros = Double.parseDouble(litrosTexto);

            if (kilometros <= 0) {
                JOptionPane.showMessageDialog(this, "Los kilómetros deben ser mayores que cero.");
                return;
            }

            if (litros <= 0) {
                JOptionPane.showMessageDialog(this, "Los litros deben ser mayores que cero.");
                return;
            }

            double rendimiento = kilometros / litros;

            listaPatentes.add(patente);
            listaKilometros.add(kilometros);
            listaLitros.add(litros);
            listaRendimientos.add(rendimiento);

            guardarEnArchivo(patente, kilometros, litros, rendimiento);

            areaTexto.append("Patente: " + patente
                    + " | Km: " + kilometros
                    + " | Litros: " + litros
                    + " | Rendimiento: " + rendimiento + " km/L\n");

            JOptionPane.showMessageDialog(this, "Consumo registrado correctamente.");

            campoPatente.setText("");
            campoKilometros.setText("");
            campoLitros.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kilómetros y litros deben ser valores numéricos válidos.");
        }
    }

    public void guardarEnArchivo(String patente, double kilometros, double litros, double rendimiento) {

        try {
            FileWriter fw = new FileWriter("consumo_vehiculos.txt", true);
            fw.write(patente + "," + kilometros + "," + litros + "," + rendimiento + "\n");
            fw.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar en el archivo.");
        }
    }

    public void mostrarRegistros() {

        areaTexto.setText("");

        try {
            BufferedReader br = new BufferedReader(new FileReader("consumo_vehiculos.txt"));

            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");

                String patente = partes[0];
                String kilometros = partes[1];
                String litros = partes[2];
                String rendimiento = partes[3];

                areaTexto.append("Patente: " + patente
                        + " | Km: " + kilometros
                        + " | Litros: " + litros
                        + " | Rendimiento: " + rendimiento + " km/L\n");
            }

            br.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo o no existen registros.");
        }
    }

    public void calcularEstadisticas() {

        areaTexto.setText("--- ESTADÍSTICAS DE RENDIMIENTO ---\n");

        try {
            BufferedReader br = new BufferedReader(new FileReader("consumo_vehiculos.txt"));

            String linea;
            int cantidad = 0;
            double suma = 0;
            double minimo = 0;
            double maximo = 0;
            boolean primerRegistro = true;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");

                double rendimiento = Double.parseDouble(partes[3]);

                suma = suma + rendimiento;
                cantidad++;

                if (primerRegistro) {
                    minimo = rendimiento;
                    maximo = rendimiento;
                    primerRegistro = false;
                } else {
                    if (rendimiento < minimo) {
                        minimo = rendimiento;
                    }

                    if (rendimiento > maximo) {
                        maximo = rendimiento;
                    }
                }
            }

            br.close();

            if (cantidad == 0) {
                JOptionPane.showMessageDialog(this, "No hay datos para calcular estadísticas.");
                return;
            }

            double promedio = suma / cantidad;

            areaTexto.append("Cantidad de registros: " + cantidad + "\n");
            areaTexto.append("Promedio de rendimiento: " + String.format("%.2f", promedio) + " km/L\n");
            areaTexto.append("Rendimiento mínimo: " + String.format("%.2f", minimo) + " km/L\n");
            areaTexto.append("Rendimiento máximo: " + String.format("%.2f", maximo) + " km/L\n");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo.");
        }
    }

    public void filtrarBajoRendimiento() {

        areaTexto.setText("--- VEHÍCULOS CON BAJO RENDIMIENTO ---\n");

        try {
            BufferedReader br = new BufferedReader(new FileReader("consumo_vehiculos.txt"));

            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");

                String patente = partes[0];
                String kilometros = partes[1];
                String litros = partes[2];
                double rendimiento = Double.parseDouble(partes[3]);

                if (rendimiento < 10) {
                    areaTexto.append("Patente: " + patente
                            + " | Km: " + kilometros
                            + " | Litros: " + litros
                            + " | Rendimiento: " + String.format("%.2f", rendimiento) + " km/L\n");

                    encontrado = true;
                }
            }

            br.close();

            if (!encontrado) {
                areaTexto.append("No existen vehículos con rendimiento menor a 10 km/L.\n");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo.");
        }
    }


    public static void main(String[] args) {
        new ControlConsumoBencina();
    }
}