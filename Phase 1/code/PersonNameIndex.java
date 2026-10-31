// A second chain orders names while the main lists stay ordered by ID.
class PersonNameIndex<T extends IPerson> {
    private Node<T> head;

    void add(T person) {
        insert(person);
        if (person instanceof Person)
            ((Person) person).attachNameIndex(this);
    }

    private void insert(T person) {
        Node<T> node = new Node<T>(person);
        Node<T> previous = null;
        Node<T> current = head;
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

    private int compare(T first, T second) {
        int comparison = first.getName().compareToIgnoreCase(second.getName());
        if (comparison != 0)
            return comparison;
        return Integer.compare(first.getId(), second.getId());
    }

    void remove(T person) {
        unlink(person);
        if (person instanceof Person)
            ((Person) person).detachNameIndex(this);
    }

    private T unlink(IPerson person) {
        Node<T> previous = null;
        Node<T> current = head;
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
        T stored = unlink(person);
        if (stored != null)
            insert(stored);
    }

    LinkedList<T> getAll() {
        LinkedList<T> result = new LinkedList<T>();
        Node<T> current = head;
        while (current != null) {
            result.insert(current.data);
            current = current.next;
        }
        return result;
    }
}
