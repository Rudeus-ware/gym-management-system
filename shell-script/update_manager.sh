# Add missing getPayments() method if needed
if ! grep -q "getPayments" src/main/java/com/gym/persistence/DataManager.java; then
    echo "Adding getPayments() method to DataManager..."
    
    cat >> src/main/java/com/gym/persistence/DataManager.java << 'EOF'

    public List<Payment> getPayments() {
        if (payments == null) {
            payments = new ArrayList<>();
        }
        return payments;
    }

    public void addPayment(Payment payment) {
        if (payments == null) {
            payments = new ArrayList<>();
        }
        payments.add(payment);
    }

    private List<Payment> payments;
EOF
fi