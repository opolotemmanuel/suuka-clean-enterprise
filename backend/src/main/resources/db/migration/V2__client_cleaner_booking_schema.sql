CREATE TABLE cleaner_applications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    user_id UUID NOT NULL,
    application_status VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    national_id VARCHAR(255),
    location VARCHAR(255),
    experience_level VARCHAR(255),
    availability VARCHAR(255),
    id_document_name VARCHAR(255),
    profile_photo_name VARCHAR(255),
    review_notes VARCHAR(2000)
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(255),
    client_id UUID NOT NULL,
    cleaner_id UUID,
    service_type VARCHAR(255) NOT NULL,
    property_address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL DEFAULT 0,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 0,
    scheduled_at TIMESTAMP,
    duration_hours INTEGER NOT NULL DEFAULT 0,
    payment_method VARCHAR(255),
    completion_notes VARCHAR(255),
    booking_status VARCHAR(255) NOT NULL
);

CREATE INDEX idx_cleaner_applications_user_id ON cleaner_applications(user_id);
CREATE INDEX idx_bookings_client_id ON bookings(client_id);
CREATE INDEX idx_bookings_cleaner_id ON bookings(cleaner_id);
CREATE INDEX idx_bookings_status ON bookings(booking_status);
