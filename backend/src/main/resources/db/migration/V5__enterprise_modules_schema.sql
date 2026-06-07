CREATE TABLE business_records (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    module VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000),
    related_entity_id VARCHAR(255),
    owner_id VARCHAR(255),
    metadata_json VARCHAR(8000)
);

CREATE TABLE task_records (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000),
    assigned_to VARCHAR(255),
    related_module VARCHAR(255) NOT NULL,
    related_entity_id VARCHAR(255),
    due_date TIMESTAMP,
    priority VARCHAR(255),
    task_status VARCHAR(255)
);

CREATE TABLE activity_timeline_events (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    module VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    details VARCHAR(4000),
    actor_id VARCHAR(255)
);

CREATE INDEX idx_business_records_module ON business_records(module);
CREATE INDEX idx_task_records_assigned_to ON task_records(assigned_to);
CREATE INDEX idx_activity_timeline_entity ON activity_timeline_events(entity_id);
