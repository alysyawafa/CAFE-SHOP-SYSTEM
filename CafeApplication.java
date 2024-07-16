import java.awt.EventQueue;
import javax.swing.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class CafeApplication {

    private JFrame frame;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField orderIdField;
    private JTextField deliveryTimeField;
    private JTextField addressField;
    private JTextField statusField;
    private JTextArea ordersTextArea;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/finalprojectbitp3123";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private List<Order> orders;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CafeApplication window = new CafeApplication();
                    window.frame.setVisible(true);
                    window.startStatusUpdateServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CafeApplication() {
        initialize();
        orders = new ArrayList<>();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblCafeDeliveryService = new JLabel("Cafe Delivery Service");
        lblCafeDeliveryService.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblCafeDeliveryService.setBounds(149, 11, 150, 21);
        frame.getContentPane().add(lblCafeDeliveryService);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(20, 40, 46, 14);
        frame.getContentPane().add(lblName);

        nameField = new JTextField();
        nameField.setBounds(80, 37, 150, 20);
        frame.getContentPane().add(nameField);
        nameField.setColumns(10);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(240, 40, 60, 14);
        frame.getContentPane().add(lblPhone);

        phoneField = new JTextField();
        phoneField.setBounds(300, 37, 120, 20);
        frame.getContentPane().add(phoneField);
        phoneField.setColumns(10);

        JLabel lblOrderId = new JLabel("Order ID:");
        lblOrderId.setBounds(20, 80, 60, 14);
        frame.getContentPane().add(lblOrderId);

        orderIdField = new JTextField();
        orderIdField.setBounds(80, 77, 150, 20);
        frame.getContentPane().add(orderIdField);
        orderIdField.setColumns(10);

        JLabel lblDeliveryTime = new JLabel("Delivery Time:");
        lblDeliveryTime.setBounds(240, 80, 90, 14);
        frame.getContentPane().add(lblDeliveryTime);

        deliveryTimeField = new JTextField();
        deliveryTimeField.setBounds(330, 77, 90, 20);
        frame.getContentPane().add(deliveryTimeField);
        deliveryTimeField.setColumns(10);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setBounds(20, 120, 60, 14);
        frame.getContentPane().add(lblAddress);

        addressField = new JTextField();
        addressField.setBounds(80, 117, 340, 20);
        frame.getContentPane().add(addressField);
        addressField.setColumns(10);

        JLabel lblStatus = new JLabel("Order Status:");
        lblStatus.setBounds(20, 160, 90, 14);
        frame.getContentPane().add(lblStatus);

        statusField = new JTextField();
        statusField.setBounds(110, 157, 150, 20);
        frame.getContentPane().add(statusField);
        statusField.setColumns(10);

        JLabel lblOrders = new JLabel("Orders:");
        lblOrders.setBounds(20, 200, 60, 14);
        frame.getContentPane().add(lblOrders);

        ordersTextArea = new JTextArea();
        ordersTextArea.setLineWrap(true);
        ordersTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(ordersTextArea);
        scrollPane.setBounds(20, 220, 400, 200);
        frame.getContentPane().add(scrollPane);

        JButton sendButton = new JButton("Send to Delivery");
        sendButton.setBounds(270, 430, 150, 23);
        frame.getContentPane().add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendOrder();
            }
        });
    }

    private void sendOrder() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String orderId = orderIdField.getText();
        String deliveryTime = deliveryTimeField.getText();
        String address = addressField.getText();
        String orderList = ordersTextArea.getText(); // Assuming ordersTextArea contains the order list

        if (name.isEmpty() || phone.isEmpty() || orderId.isEmpty() || deliveryTime.isEmpty() || address.isEmpty() || orderList.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields before sending.");
            return;
        }

        // Send order to receiver application
        try (Socket socket = new Socket("localhost", 8080)) {  // Assuming receiver is on localhost
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);
            out.println(phone);  // Make sure to send the phone number
            out.println(orderId);
            out.println(deliveryTime);
            out.println(address);
            out.println(orderList);
            socket.close();
            JOptionPane.showMessageDialog(frame, "Order sent successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error sending order: " + ex.getMessage());
        }

        // Insert order into the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Orders (name, phone, orderId, deliveryTime, address, orderlist, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);  // Make sure to insert the phone number
                pstmt.setString(3, orderId);
                pstmt.setString(4, deliveryTime);
                pstmt.setString(5, address);
                pstmt.setString(6, orderList);
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving order to database: " + ex.getMessage());
            return;
        }
    }

    private void startStatusUpdateServer() {
        Thread serverThread = new Thread(new Runnable() {
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(8081)) { // Change port number if needed
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new StatusUpdateHandler(clientSocket)).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }

    private class StatusUpdateHandler implements Runnable {
        private Socket clientSocket;

        public StatusUpdateHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String orderId = in.readLine();
                String status = in.readLine();

                updateOrderStatus(orderId, status);

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Status update received for Order ID: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void updateOrderStatus(String orderId, String status) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE Orders SET status = ? WHERE orderId = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, status);
                    pstmt.setString(2, orderId);
                    pstmt.executeUpdate();
                }

                // Update the status in the GUI
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        statusField.setText(status);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Order {
        private String name;
        private String phone;
        private String orderId;
        private String deliveryTime;
        private String address;
        private String orderList;

        public Order(String name, String phone, String orderId, String deliveryTime, String address, String orderList) {
            this.name = name;
            this.phone = phone;
            this.orderId = orderId;
            this.deliveryTime = deliveryTime;
            this.address = address;
            this.orderList = orderList;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public String getAddress() {
            return address;
        }

        public String getOrderList() {
            return orderList;
        }
    }
}
