CREATE TABLE bank_stocks (
                             stock_name VARCHAR(100) PRIMARY KEY,
                             quantity   INT NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE wallet_stocks (
                               wallet_id  VARCHAR(100) NOT NULL,
                               stock_name VARCHAR(100) NOT NULL,
                               quantity   INT NOT NULL CHECK (quantity >= 0),
                               PRIMARY KEY (wallet_id, stock_name)
);

CREATE TABLE audit_log (
                           id         BIGSERIAL PRIMARY KEY,
                           type       VARCHAR(4) NOT NULL CHECK (type IN ('buy', 'sell')),
                           wallet_id  VARCHAR(100) NOT NULL,
                           stock_name VARCHAR(100) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW()
);