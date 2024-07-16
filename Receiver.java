import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class Receiver extends JFrame {

    private JPanel contentPane;
    private JTextField orderIdField;
    private JTextField deliveryTimeField;
    private JTextField addressField;
    private JComboBox<String> statusComboBox;
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;

    private List<Order> orders;
    private ServerSocket serverSocket;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/finalprojectbitp3123";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private JScrollPane scrollPane;
    private JTextArea textArea;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Receiver frame = new Receiver();
                    frame.setVisible(true);
                    frame.startServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Receiver() {
        orders = new ArrayList<>();

        setTitle("Receiver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblOrderId = new JLabel("Order ID");
        lblOrderId.setBounds(10, 11, 65, 14);
        contentPane.add(lblOrderId);

        orderIdField = new JTextField();
        orderIdField.setBounds(85, 8, 86, 20);
        contentPane.add(orderIdField);
        orderIdField.setColumns(10);

        JLabel lblDeliveryTime = new JLabel("Delivery Time");
        lblDeliveryTime.setBounds(10, 42, 86, 14);
        contentPane.add(lblDeliveryTime);

        deliveryTimeField = new JTextField();
        deliveryTimeField.setBounds(106, 39, 86, 20);
        contentPane.add(deliveryTimeField);
        deliveryTimeField.setColumns(10);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setBounds(10, 73, 65, 14);
        contentPane.add(lblAddress);

        addressField = new JTextField();
        addressField.setBounds(85, 70, 86, 20);
        contentPane.add(addressField);
        addressField.setColumns(10);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setBounds(10, 104, 46, 14);
        contentPane.add(lblStatus);

        statusComboBox = new JComboBox<>();
        statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"Accept", "Decline", "Done"}));
        statusComboBox.setBounds(85, 101, 86, 20);
        contentPane.add(statusComboBox);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOrder();
            }
        });
        btnUpdate.setBounds(335, 70, 89, 23);
        contentPane.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });
        btnDelete.setBounds(335, 101, 89, 23);
        contentPane.add(btnDelete);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setBounds(10, 150, 414, 100);
        contentPane.add(orderList);
        
        scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 150, 414, 100);
        contentPane.add(scrollPane);
        
        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);

        orderList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = orderList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Order order = orders.get(index);
                        orderIdField.setText(order.getOrderId());
                        deliveryTimeField.setText(order.getDeliveryTime());
                        addressField.setText(order.getAddress());
                        statusComboBox.setSelectedItem(order.getStatus());
                    }
                }
            }
        });
    }

    private void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(8080);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String name = in.readLine();
                        String phone = in.readLine();  // Read the phone number
                        String orderId = in.readLine();
                        String deliveryTime = in.readLine();
                        String address = in.readLine();
                        addOrder(orderId, deliveryTime, address, phone);
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addOrder(String orderId, String deliveryTime, String address, String phone) {
        Order order = new Order(orderId, deliveryTime, address, phone, "Pending");
        orders.add(order);
        orderListModel.addElement(order.toString());

        // Insert order into the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Orders (orderId, deliveryTime, address, phone, status) VALUES (?, ?, ?, ?, 'Pending')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, orderId);
                pstmt.setString(2, deliveryTime);
                pstmt.setString(3, address);
                pstmt.setString(4, phone);  // Include the phone number
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving order to database: " + ex.getMessage());
        }
    }

    private void updateOrder() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex != -1) {
            Order order = orders.get(selectedIndex);
            order.setOrderId(orderIdField.getText());
            order.setDeliveryTime(deliveryTimeField.getText());
            order.setAddress(addressField.getText());
            order.setStatus((String) statusComboBox.getSelectedItem());

            orderListModel.set(selectedIndex, order.toString());

            // Update order status in the database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE Orders SET deliveryTime = ?, address = ?, status = ? WHERE orderId = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, deliveryTimeField.getText());
                    pstmt.setString(2, addressField.getText());
                    pstmt.setString(3, (String) statusComboBox.getSelectedItem());
                    pstmt.setString(4, orderIdField.getText());
                    pstmt.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating order in database: " + ex.getMessage());
            }

            sendStatusUpdateToCafe(order.getOrderId(), order.getStatus());
        }
    }

    private void deleteOrder() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex != -1) {
            Order order = orders.remove(selectedIndex);
            orderListModel.remove(selectedIndex);

            // Delete order from the database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM Orders WHERE orderId = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, order.getOrderId());
                    pstmt.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting order from database: " + ex.getMessage());
            }
        }
    }

    private void sendStatusUpdateToCafe(String orderId, String status) {
        try {
            Socket socket = new Socket("localhost", 8081);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(orderId);
            out.println(status);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Order {
        private String orderId;
        private String deliveryTime;
        private String address;
        private String phone;
        private String status;

        public Order(String orderId, String deliveryTime, String address, String phone, String status) {
            this.orderId = orderId;
            this.deliveryTime = deliveryTime;
            this.address = address;
            this.phone = phone;
            this.status = status;
            this.phone = phone;
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(String deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Order ID: " + orderId + ", Delivery Time: " + deliveryTime + ", Address: " + address + ", Phone: " + phone + ", Status: " + status;
        }
    }

    private void updateOrderFields(Order order) {
        orderIdField.setText(order.getOrderId());
        deliveryTimeField.setText(order.getDeliveryTime());
        addressField.setText(order.getAddress());
        statusComboBox.setSelectedItem(order.getStatus());
    }

    private Order getOrderFromFields() {
        return new Order(
            orderIdField.getText(),
            deliveryTimeField.getText(),
            addressField.getText(),
            "Unknown", // phone number is not updated here
            (String) statusComboBox.getSelectedItem()
        );
    }
}
