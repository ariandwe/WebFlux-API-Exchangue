-- Tabla para tipos de cambio
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moneda_origen VARCHAR(10) NOT NULL,
    moneda_destino VARCHAR(10) NOT NULL,
    tipo_cambio DECIMAL(20, 6) NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    UNIQUE(moneda_origen, moneda_destino)
);

-- Tabla para logs de auditoría
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    moneda_origen VARCHAR(10) NOT NULL,
    moneda_destino VARCHAR(10) NOT NULL,
    monto_inicial DECIMAL(20, 2) NOT NULL,
    monto_convertido DECIMAL(20, 2) NOT NULL,
    tipo_cambio_aplicado DECIMAL(20, 6) NOT NULL,
    fecha TIMESTAMP NOT NULL
);

