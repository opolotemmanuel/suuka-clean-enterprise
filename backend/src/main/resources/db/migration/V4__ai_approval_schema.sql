CREATE TABLE approval_requests (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    approval_type VARCHAR(255) NOT NULL,
    approval_status VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    reason VARCHAR(4000),
    requested_by VARCHAR(255),
    reviewed_by VARCHAR(255)
);

CREATE TABLE ai_recommendations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    module VARCHAR(255),
    recommendation_title VARCHAR(255),
    recommendation_body VARCHAR(4000),
    priority VARCHAR(255),
    confidence_score DOUBLE PRECISION NOT NULL DEFAULT 0,
    risk_level VARCHAR(255),
    generated_for_role VARCHAR(255),
    generated_for_user VARCHAR(255),
    approved_by VARCHAR(255),
    ai_status VARCHAR(255)
);

CREATE INDEX idx_approval_requests_status ON approval_requests(approval_status);
CREATE INDEX idx_ai_recommendations_role ON ai_recommendations(generated_for_role);
