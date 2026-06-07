CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    branch VARCHAR(255),
    zone VARCHAR(255),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    phone_number VARCHAR(255),
    address VARCHAR(255),
    profile_picture_url VARCHAR(255),
    account_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_code VARCHAR(255),
    verification_code_expires_at TIMESTAMP,
    verification_attempts INTEGER NOT NULL DEFAULT 0,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    mfa_code VARCHAR(255),
    mfa_code_expires_at TIMESTAMP,
    mfa_attempts INTEGER NOT NULL DEFAULT 0,
    reset_token VARCHAR(255),
    reset_token_expires_at TIMESTAMP
);

CREATE TABLE user_permissions (
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, permission)
);

CREATE TABLE role_permissions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    permission VARCHAR(255) NOT NULL,
    CONSTRAINT uk_role_permissions_role_permission UNIQUE (role, permission)
);

CREATE TABLE auth_sessions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    user_id UUID NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    device_label VARCHAR(255),
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    action VARCHAR(255) NOT NULL,
    actor_id VARCHAR(255),
    module VARCHAR(255),
    details VARCHAR(4000)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    user_id UUID NOT NULL,
    target_role VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(2000),
    related_module VARCHAR(255) NOT NULL,
    related_entity_id VARCHAR(255),
    read BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE notification_actions (
    notification_id UUID NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    action VARCHAR(255) NOT NULL
);

CREATE TABLE message_conversations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    participant_one_id UUID NOT NULL,
    participant_two_id UUID NOT NULL,
    related_module VARCHAR(255),
    related_entity_id VARCHAR(255)
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    conversation_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    body VARCHAR(2000) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_auth_sessions_user_id ON auth_sessions(user_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_recipient_id ON messages(recipient_id);
