INSERT INTO public.customers (username)
VALUES ('Sancho'),
       ('Gregor'),
       ('test');

INSERT INTO public.accounts (customer_id, balance, currency)
VALUES (1, 35.78, 'EUR'),
       (1, 165.87, 'CHF'),
       (2, 45.45, 'ARS'),
       (3, 458.67, 'EUR'),
       (3, 1534.15, 'BSD'),
       (3, 4570.45, 'AED');

INSERT INTO public.transfers (amount, date_time, source_account_id, target_account_id)
VALUES (100.58, TIMESTAMP '2024-02-02 11:23:44', 1, 3),
       (10.00, TIMESTAMP '2024-02-04 10:23:25', 1, 3),
       (45.78, TIMESTAMP '2024-02-06 19:23:15', 1, 3);