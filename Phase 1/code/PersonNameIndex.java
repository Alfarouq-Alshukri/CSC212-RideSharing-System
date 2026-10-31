// A second chain orders names while the main lists stay ordered by ID.
class PersonNameIndex {
    private Node<IPerson> head;

    void add(IPerson person) {
        insert(person);
        if (person instanceof Person)
            ((Person) person).attachNameIndex(this);
    }

    private void insert(IPerson person) {
        Node<IPerson> node = new Node<IPerson>(person);
        Node<IPerson> previous = null;
        Node<IPerson> current = head;
        while (current != null && compare(current.data, person) <= 0) {
            previous = current;
            current = current.next;
        }
        node.next = current;
        if (previous == null)
            head = node;
        else
            previous.next = node;
    }

    private int compare(IPerson first, IPerson second) {
        int comparison = first.getName().compareToIgnoreCase(second.getName());
        if (comparison != 0)
            return comparison;
        return Integer.compare(first.getId(), second.getId());
    }

    void remove(IPerson person) {
        unlink(person);
        if (person instanceof Person)
            ((Person) person).detachNameIndex(this);
    }

    private IPerson unlink(IPerson person) {
        Node<IPerson> previous = null;
        Node<IPerson> current = head;
        while (current != null) {
            if (current.data == person) {
                if (previous == null)
                    head = current.next;
                else
                    previous.next = current.next;
                return current.data;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    void nameChanged(IPerson person) {
        IPerson stored = unlink(person);
        if (stored != null)
            insert(stored);
    }

    LinkedList<IRider> getRiders() {
        LinkedList<IRider> result = new LinkedList<IRider>();
        Node<IPerson> current = head;
        while (current != null) {
            result.insert((IRider) current.data);
            current = current.next;
        }
        return result;
    }

    LinkedList<IDriver> getDrivers() {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        Node<IPerson> current = head;
        while (current != null) {
            result.insert((IDriver) current.data);
            current = current.next;
        }
        return result;
    }
}
