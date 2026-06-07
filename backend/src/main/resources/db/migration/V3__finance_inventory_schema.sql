CREATE TABLE inventory_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255),
    quantity INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 0,
    unit VARCHAR(255)
);

CREATE TABLE supply_requests (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    cleaner_id UUID,
    inventory_item_id UUID,
    quantity INTEGER NOT NULL DEFAULT 0,
    reason VARCHAR(255),
    approved BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    phone VARCHAR(255),
    rating INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    supplier_id UUID,
    description VARCHAR(255),
    amount NUMERIC(38, 2),
    approved BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_supply_requests_cleaner_id ON supply_requests(cleaner_id);
CREATE INDEX idx_supply_requests_inventory_item_id ON supply_requests(inventory_item_id);
CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders(supplier_id);
