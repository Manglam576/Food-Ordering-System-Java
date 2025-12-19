import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

class FoodItem {
    String name;
    String desc;
    double price;
    String imagePath;
    int quantity;
    String category;

    FoodItem(String name, String desc, double price, String imagePath, String category) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imagePath = imagePath;
        this.quantity = 0;
        this.category = category;
    }
}

public class FoodOrderingApp extends JFrame {

    java.util.List<FoodItem> menu = new ArrayList<>();
    java.util.List<FoodItem> filteredMenu = new ArrayList<>();

    JTextArea cartArea;
    JLabel subtotalLabel, gstLabel, totalLabel;
    JComboBox<String> paymentBox;
    JTextField searchField;
    JPanel menuPanel;

    Map<String, Integer> cartMap = new LinkedHashMap<>();
    Map<String, Double> priceMap = new HashMap<>();

    double subtotal = 0.0, gst = 0.0, total = 0.0;

    String currentCategory = "All";
    String currentSearch = "";

    Color saffron = new Color(255, 145, 77);
    Color accent = new Color(255, 111, 0);
    Color background = new Color(248, 248, 252);

    public FoodOrderingApp() {
        setTitle("Appetitto - Food Ordering System");
        setSize(1150, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(background);
        setLocationRelativeTo(null);

        loadMenu();

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(createCategoryBar(), BorderLayout.SOUTH);

        menuPanel = createMenuPanel();
        JPanel cartPanel = createCartPanel();

        add(top, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(cartPanel, BorderLayout.EAST);
    }

    void loadMenu() {
        menu.add(new FoodItem("Burger","Juicy cheesy burger with lettuce & sauce",120,"images/burger.jpg","Fast Food"));
        menu.add(new FoodItem("French Fries","Crispy golden fries with salt & herbs",100,"images/fries.jpg","Fast Food"));
        menu.add(new FoodItem("Cold Drink","Refreshing chilled soda or cola",60,"images/colddrink.jpg","Drinks"));
        menu.add(new FoodItem("Paneer Wrap","Spicy paneer wrapped in a soft roll",140,"images/wrap.jpg","Fast Food"));
        menu.add(new FoodItem("Margherita","Classic margherita pizza",140,"images/margherita.jpg","Fast Food"));

        menu.add(new FoodItem("Waffles","Golden crispy waffles",85,"images/waffles.jpg","Desserts"));
        menu.add(new FoodItem("Caeser Salad","Romaine lettuce salad",110,"images/caesarsalad.jpg","Others"));
        menu.add(new FoodItem("Chocolate Brownie","Rich chocolate brownie",90,"images/brownie.jpg","Desserts"));
        menu.add(new FoodItem("Pasta Alfredo","Creamy Alfredo pasta",150,"images/pasta.jpg","Others"));
        menu.add(new FoodItem("Grilled Sandwich","Veg sandwich with cheese",130,"images/sandwich.jpg","Fast Food"));
        menu.add(new FoodItem("Veggie Sushi","Fresh vegetable sushi rolls",180,"images/sushi.jpg","Others"));
        menu.add(new FoodItem("Fruit Smoothie","Blend of fresh fruits and yogurt",95,"images/smoothie.jpg","Drinks"));

        filteredMenu.addAll(menu);
        for (FoodItem it : menu) priceMap.put(it.name, it.price);
    }

    JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 90));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, saffron));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        logoPanel.setBackground(Color.WHITE);

        JLabel logoLabel;
        try {
            ImageIcon icon = new ImageIcon("images/logo.jpg");
            Image scaledImg = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImg));
        } catch (Exception e) {
            logoLabel = new JLabel("🍽️");
            logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        }

        JLabel nameLabel = new JLabel("Appetitto");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        nameLabel.setForeground(accent);

        JLabel tagline = new JLabel("Fresh. Fast. Flavorful.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tagline.setForeground(new Color(90, 90, 90));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setBackground(Color.WHITE);
        titleBox.add(nameLabel);
        titleBox.add(tagline);

        logoPanel.add(logoLabel);
        logoPanel.add(titleBox);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(25);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(new RoundBorder(20));
        searchField.setToolTipText("Search your favorite dish...");
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                currentSearch = searchField.getText().trim().toLowerCase();
                applyFilters();
            }
        });

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    JPanel createCategoryBar() {
        JPanel catPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        catPanel.setBackground(Color.WHITE);

        String[][] cats = {
                {"All", "✨", "All"},
                {"Fast Food", "🍔", "Fast Food"},
                {"Desserts", "🍰", "Desserts"},
                {"Drinks", "🥤", "Drinks"},
                {"Others", "🍱", "Others"}
        };

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < cats.length; i++) {
            String label = cats[i][0];
            String emoji = cats[i][1];
            String key = cats[i][2];

            JToggleButton btn = new JToggleButton(emoji + "  " + label);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(150, 36));
            btn.setBackground(new Color(245,245,245));
            btn.setBorder(new LineBorder(new Color(220,220,220),1,true));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (i == 0) {
                btn.setSelected(true);
                styleCategorySelected(btn);
            }

            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    currentCategory = key;
                    styleCategorySelected(btn);
                    applyFilters();
                } else {
                    styleCategoryNormal(btn);
                }
            });

            styleCategoryNormal(btn);
            group.add(btn);
            catPanel.add(btn);
        }

        return catPanel;
    }

    void styleCategorySelected(AbstractButton b) {
        b.setBackground(new Color(255, 238, 225));
        b.setForeground(accent.darker());
        b.setBorder(new LineBorder(accent, 1, true));
    }

    void styleCategoryNormal(AbstractButton b) {
        if (!b.isSelected()) {
            b.setBackground(new Color(245,245,245));
            b.setForeground(Color.BLACK);
            b.setBorder(new LineBorder(new Color(220,220,220),1,true));
        }
    }

    JPanel createMenuPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(background);

        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(background);

        for (FoodItem item : filteredMenu)
            panel.add(createMenuCard(item));

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(15);

        wrapper.add(sp);
        return wrapper;
    }

    JPanel createMenuCard(FoodItem item) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230,230,230), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel imgLabel;
        try {
            ImageIcon icon = scaleImageKeepRatio(item.imagePath, 220, 150);
            imgLabel = new JLabel(icon);
        } catch (Exception e) {
            imgLabel = new JLabel("No Image", SwingConstants.CENTER);
            imgLabel.setForeground(Color.GRAY);
        }
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel name = new JLabel(item.name, SwingConstants.CENTER);
        name.setFont(new Font("SansSerif", Font.BOLD, 17));

        JLabel desc = new JLabel("<html><center>" + item.desc + "</center></html>", SwingConstants.CENTER);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(new Color(90,90,90));

        JLabel price = new JLabel("₹" + item.price, SwingConstants.CENTER);
        price.setFont(new Font("SansSerif", Font.BOLD, 15));
        price.setForeground(accent);

        JPanel mid = new JPanel(new GridLayout(3,1));
        mid.setBackground(Color.WHITE);
        mid.add(name); mid.add(desc); mid.add(price);

        JPanel qty = new JPanel();
        qty.setBackground(Color.WHITE);

        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        styleSmallButton(minus);
        styleSmallButton(plus);

        JLabel q = new JLabel("Qty: " + item.quantity);
        q.setFont(new Font("SansSerif", Font.BOLD, 13));

        minus.addActionListener(e -> {
            if(item.quantity > 0) item.quantity--;
            q.setText("Qty: " + item.quantity);
            updateCart(item);
        });

        plus.addActionListener(e -> {
            item.quantity++;
            q.setText("Qty: " + item.quantity);
            updateCart(item);
        });

        qty.add(minus); qty.add(q); qty.add(plus);

        card.add(imgLabel,BorderLayout.NORTH);
        card.add(mid,BorderLayout.CENTER);
        card.add(qty,BorderLayout.SOUTH);

        return card;
    }

    ImageIcon scaleImageKeepRatio(String path, int maxW, int maxH) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return icon;

        double scale = Math.min((double)maxW / w, (double)maxH / h);
        int newW = (int)(w * scale);
        int newH = (int)(h * scale);

        Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    JPanel createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout(10,10));
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(new CompoundBorder(
                new MatteBorder(0,2,0,0,new Color(235,235,235)),
                new EmptyBorder(10,10,10,10)
        ));
        cartPanel.setPreferredSize(new Dimension(320, 10));

        JLabel title = new JLabel("🛒 Cart & Billing",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,18));
        title.setForeground(accent);
        cartPanel.add(title,BorderLayout.NORTH);

        cartArea = new JTextArea();
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(cartArea);
        sp.setBorder(new LineBorder(new Color(235,235,235)));
        cartPanel.add(sp,BorderLayout.CENTER);

        subtotalLabel = new JLabel("Subtotal: ₹0.00");
        gstLabel = new JLabel("GST (5%): ₹0.00");
        totalLabel = new JLabel("Total: ₹0.00");

        paymentBox = new JComboBox<>(new String[]{"Card","UPI","Cash"});

        JButton order = new JButton("Place Order");
        stylePrimaryButton(order);
        order.addActionListener(e -> placeOrder());

        JPanel bottom = new JPanel(new GridLayout(0,1,5,5));
        bottom.setBackground(Color.WHITE);
        bottom.add(subtotalLabel);
        bottom.add(gstLabel);
        bottom.add(totalLabel);
        bottom.add(new JLabel("Payment Mode:"));
        bottom.add(paymentBox);
        bottom.add(order);

        cartPanel.add(bottom,BorderLayout.SOUTH);

        return cartPanel;
    }

    void applyFilters() {
        filteredMenu.clear();

        for (FoodItem f : menu) {
            boolean matchCategory = currentCategory.equals("All") ||
                    f.category.equalsIgnoreCase(currentCategory);
            boolean matchSearch = currentSearch.isEmpty() ||
                    f.name.toLowerCase().contains(currentSearch);

            if (matchCategory && matchSearch) filteredMenu.add(f);
        }

        getContentPane().remove(menuPanel);
        menuPanel = createMenuPanel();
        add(menuPanel,BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    void updateCart(FoodItem f) {
        if(f.quantity==0) cartMap.remove(f.name);
        else cartMap.put(f.name,f.quantity);
        refreshCart();
    }

    void refreshCart() {
        cartArea.setText("");
        subtotal = 0;

        for (String item : cartMap.keySet()) {
            int q = cartMap.get(item);
            double p = priceMap.get(item);
            subtotal += p*q;
            cartArea.append(item+" x "+q+" = ₹"+String.format("%.2f",p*q)+"\n");
        }

        gst = subtotal * 0.05;
        total = subtotal + gst;

        subtotalLabel.setText("Subtotal: ₹"+String.format("%.2f",subtotal));
        gstLabel.setText("GST (5%): ₹"+String.format("%.2f",gst));
        totalLabel.setText("Total: ₹"+String.format("%.2f",total));
    }

    void placeOrder() {
        if(cartMap.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Cart is empty!");
            return;
        }
        showPopup();
    }

    void showPopup() {
        JDialog d = new JDialog(this,true);
        d.setSize(330,240);
        d.setUndecorated(true);
        d.setLocationRelativeTo(this);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(new Color(210,210,210),2,true));

        JLabel tick = new JLabel("✔",SwingConstants.CENTER);
        tick.setFont(new Font("SansSerif",Font.BOLD,70));
        tick.setForeground(new Color(56, 142, 60));

        JLabel msg = new JLabel("Order Confirmed!",SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif",Font.BOLD,18));
        msg.setForeground(new Color(56, 142, 60));

        JButton ok = new JButton("Close");
        stylePrimaryButton(ok);
        ok.setForeground(Color.BLUE);
        ok.addActionListener(e -> d.dispose());

        p.add(tick,BorderLayout.NORTH);
        p.add(msg,BorderLayout.CENTER);
        p.add(ok,BorderLayout.SOUTH);

        d.add(p);
        d.setVisible(true);
    }

    void styleSmallButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(40,40,40));
        btn.setBorder(new LineBorder(new Color(200,200,200),1,true));
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(40,25));
    }

    void stylePrimaryButton(JButton btn) {
        btn.setBackground(accent);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new LineBorder(accent.darker(),1,true));
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    static class RoundBorder extends LineBorder {
        int arc;
        RoundBorder(int radius) {
            super(new Color(200,200,200), 1, true);
            this.arc = radius;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodOrderingApp().setVisible(true));
    }
}
