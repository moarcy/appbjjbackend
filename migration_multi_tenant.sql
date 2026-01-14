-- Migration script for multi-tenant implementation
-- Add school_id to all business entities and rename ativo to deleted_at

-- Create schools table
CREATE TABLE schools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    deleted_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes for schools
CREATE INDEX idx_school_slug ON schools(slug);
CREATE INDEX idx_school_status ON schools(status);

-- Add school_id to users
ALTER TABLE users ADD COLUMN school_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_school FOREIGN KEY (school_id) REFERENCES schools(id);
CREATE INDEX idx_users_school_id ON users(school_id);
-- Rename ativo to deleted_at for users
ALTER TABLE users CHANGE ativo deleted_at DATETIME;

-- Add school_id to turmas
ALTER TABLE turmas ADD COLUMN school_id BIGINT NOT NULL;
ALTER TABLE turmas ADD CONSTRAINT fk_turmas_school FOREIGN KEY (school_id) REFERENCES schools(id);
CREATE INDEX idx_turmas_school_id ON turmas(school_id);
-- Rename ativo to deleted_at for turmas
ALTER TABLE turmas CHANGE ativo deleted_at DATETIME;

-- Add school_id to professores
ALTER TABLE professores ADD COLUMN school_id BIGINT NOT NULL;
ALTER TABLE professores ADD CONSTRAINT fk_professores_school FOREIGN KEY (school_id) REFERENCES schools(id);
CREATE INDEX idx_professores_school_id ON professores(school_id);
-- Rename ativo to deleted_at for professores
ALTER TABLE professores CHANGE ativo deleted_at DATETIME;

-- Add school_id to chamadas
ALTER TABLE chamadas ADD COLUMN school_id BIGINT NOT NULL;
ALTER TABLE chamadas ADD CONSTRAINT fk_chamadas_school FOREIGN KEY (school_id) REFERENCES schools(id);
CREATE INDEX idx_chamadas_school_id ON chamadas(school_id);
-- Rename ativo to deleted_at for chamadas
ALTER TABLE chamadas CHANGE ativo deleted_at DATETIME;

-- Add school_id to user_historico
ALTER TABLE user_historico ADD COLUMN school_id BIGINT NOT NULL;
ALTER TABLE user_historico ADD CONSTRAINT fk_user_historico_school FOREIGN KEY (school_id) REFERENCES schools(id);
CREATE INDEX idx_user_historico_school_id ON user_historico(school_id);

-- Update unique constraints for turmas to include school_id
-- Assuming existing constraint, drop and recreate
-- ALTER TABLE turmas DROP CONSTRAINT unique_turma_modalidade_horario;
-- ALTER TABLE turmas ADD CONSTRAINT uk_turmas_school_modalidade_horario UNIQUE (school_id, modalidade, horario);

-- For users, add unique constraint on school_id + username if needed
-- ALTER TABLE users ADD CONSTRAINT uk_users_school_username UNIQUE (school_id, username);

-- Insert initial SUPER_ADMIN user if needed (adjust as per app)
-- INSERT INTO users (nome, username, password, role, ativo) VALUES ('Super Admin', 'superadmin', 'hashed_password', 'SUPER_ADMIN', true);

-- Note: Populate school_id for existing data manually or via scripts, as it's not nullable for most.
