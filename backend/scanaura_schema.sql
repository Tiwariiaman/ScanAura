--
-- PostgreSQL database dump
--

\restrict 4eBRf49egt96TW32CTpHMvfdoivIJTrHKOsqHTQHbgP0NIU5sP02axFlhPDbecI

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: businesses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.businesses (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    address character varying(255),
    business_name character varying(255) NOT NULL,
    business_type character varying(255) NOT NULL,
    city character varying(255),
    country character varying(255),
    description character varying(1000),
    email character varying(255),
    logo_url character varying(255),
    phone character varying(255) NOT NULL,
    pincode character varying(255),
    qr_slug character varying(255) NOT NULL,
    state character varying(255),
    upi_id character varying(255),
    website character varying(255),
    whatsapp character varying(255),
    owner_id uuid NOT NULL,
    CONSTRAINT businesses_business_type_check CHECK (((business_type)::text = ANY ((ARRAY['FOOD'::character varying, 'RETAIL'::character varying, 'ECOMMERCE'::character varying, 'SERVICES'::character varying, 'PERSONAL_BRAND'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: catalogs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.catalogs (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    available boolean NOT NULL,
    best_seller boolean NOT NULL,
    description character varying(1000),
    display_order integer NOT NULL,
    image_url character varying(255),
    name character varying(255) NOT NULL,
    price numeric(10,2) NOT NULL,
    recommended boolean NOT NULL,
    veg boolean NOT NULL,
    business_id uuid NOT NULL,
    category_id uuid
);


--
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    display_order integer NOT NULL,
    name character varying(255) NOT NULL,
    business_id uuid NOT NULL
);


--
-- Name: email_verification_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.email_verification_tokens (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    expires_at timestamp(6) without time zone NOT NULL,
    token character varying(100) NOT NULL,
    used boolean NOT NULL,
    user_id uuid NOT NULL
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: password_reset_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.password_reset_tokens (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    expires_at timestamp(6) without time zone NOT NULL,
    token character varying(100) NOT NULL,
    used boolean NOT NULL,
    user_id uuid NOT NULL
);


--
-- Name: plans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.plans (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    ai_import_limit integer NOT NULL,
    branded_qr boolean NOT NULL,
    monthly_price numeric(10,2) NOT NULL,
    name character varying(255) NOT NULL,
    priority_support boolean NOT NULL,
    trial_days integer NOT NULL,
    yearly_price numeric(10,2) NOT NULL
);


--
-- Name: qr_codes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qr_codes (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    assigned boolean NOT NULL,
    qr_code character varying(30) NOT NULL,
    type character varying(255) NOT NULL,
    business_id uuid,
    CONSTRAINT qr_codes_type_check CHECK (((type)::text = ANY ((ARRAY['DIGITAL'::character varying, 'PHYSICAL'::character varying])::text[])))
);


--
-- Name: subscription_requests; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subscription_requests (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    admin_remark character varying(500),
    billing_cycle character varying(255) NOT NULL,
    payment_screenshot_url character varying(500) NOT NULL,
    status character varying(255) NOT NULL,
    transaction_id character varying(100) NOT NULL,
    business_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    CONSTRAINT subscription_requests_billing_cycle_check CHECK (((billing_cycle)::text = ANY ((ARRAY['MONTHLY'::character varying, 'YEARLY'::character varying])::text[]))),
    CONSTRAINT subscription_requests_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: subscriptions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subscriptions (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    admin_remark character varying(500),
    ai_import_used integer NOT NULL,
    approved_at timestamp(6) without time zone,
    billing_cycle character varying(255) NOT NULL,
    end_date date NOT NULL,
    payment_screenshot_url character varying(500),
    start_date date NOT NULL,
    status character varying(255) NOT NULL,
    transaction_id character varying(100),
    business_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    CONSTRAINT subscriptions_billing_cycle_check CHECK (((billing_cycle)::text = ANY ((ARRAY['MONTHLY'::character varying, 'YEARLY'::character varying])::text[]))),
    CONSTRAINT subscriptions_status_check CHECK (((status)::text = ANY ((ARRAY['TRIAL'::character varying, 'PENDING'::character varying, 'ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    active boolean NOT NULL,
    email character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    mobile character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    verified boolean NOT NULL,
    deleted boolean NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'BUSINESS_OWNER'::character varying])::text[])))
);


--
-- Name: businesses businesses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.businesses
    ADD CONSTRAINT businesses_pkey PRIMARY KEY (id);


--
-- Name: catalogs catalogs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.catalogs
    ADD CONSTRAINT catalogs_pkey PRIMARY KEY (id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: email_verification_tokens email_verification_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.email_verification_tokens
    ADD CONSTRAINT email_verification_tokens_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: email_verification_tokens idx_email_verification_token; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.email_verification_tokens
    ADD CONSTRAINT idx_email_verification_token UNIQUE (token);


--
-- Name: password_reset_tokens idx_password_reset_token; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT idx_password_reset_token UNIQUE (token);


--
-- Name: password_reset_tokens password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id);


--
-- Name: plans plans_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plans
    ADD CONSTRAINT plans_pkey PRIMARY KEY (id);


--
-- Name: qr_codes qr_codes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qr_codes
    ADD CONSTRAINT qr_codes_pkey PRIMARY KEY (id);


--
-- Name: subscription_requests subscription_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscription_requests
    ADD CONSTRAINT subscription_requests_pkey PRIMARY KEY (id);


--
-- Name: subscriptions subscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT subscriptions_pkey PRIMARY KEY (id);


--
-- Name: users uk63cf888pmqtt5tipcne79xsbm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk63cf888pmqtt5tipcne79xsbm UNIQUE (mobile);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: qr_codes ukh069f6cl4qrxsje0i80qk0p5o; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qr_codes
    ADD CONSTRAINT ukh069f6cl4qrxsje0i80qk0p5o UNIQUE (qr_code);


--
-- Name: businesses ukhsny9fimfdrp6vlmsygkrtf5y; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.businesses
    ADD CONSTRAINT ukhsny9fimfdrp6vlmsygkrtf5y UNIQUE (qr_slug);


--
-- Name: plans ukj2syv9y60858xbq169nbeg7ea; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plans
    ADD CONSTRAINT ukj2syv9y60858xbq169nbeg7ea UNIQUE (name);


--
-- Name: password_reset_tokens ukla2ts67g4oh2sreayswhox1i6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT ukla2ts67g4oh2sreayswhox1i6 UNIQUE (user_id);


--
-- Name: subscriptions ukr13wticerot66fejnp4s310f3; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT ukr13wticerot66fejnp4s310f3 UNIQUE (business_id);


--
-- Name: email_verification_tokens uks3mje1c85ftmp2uld6dt1bffs; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.email_verification_tokens
    ADD CONSTRAINT uks3mje1c85ftmp2uld6dt1bffs UNIQUE (user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: categories fk1uihumt7aa97ydn1x1qx415ln; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT fk1uihumt7aa97ydn1x1qx415ln FOREIGN KEY (business_id) REFERENCES public.businesses(id);


--
-- Name: catalogs fk3ekwt7v0npb883rka47mpn3a0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.catalogs
    ADD CONSTRAINT fk3ekwt7v0npb883rka47mpn3a0 FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: subscription_requests fk3vxfhbea0cvautpn2el1fg7pq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscription_requests
    ADD CONSTRAINT fk3vxfhbea0cvautpn2el1fg7pq FOREIGN KEY (business_id) REFERENCES public.businesses(id);


--
-- Name: subscription_requests fk5n9lsu8a2uim7fvaecfrasvfw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscription_requests
    ADD CONSTRAINT fk5n9lsu8a2uim7fvaecfrasvfw FOREIGN KEY (plan_id) REFERENCES public.plans(id);


--
-- Name: subscriptions fkb1uf5qnxi6uj95se8ykydntl1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fkb1uf5qnxi6uj95se8ykydntl1 FOREIGN KEY (plan_id) REFERENCES public.plans(id);


--
-- Name: subscriptions fkd75uwj5b3erhwwt5flxnevr7o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fkd75uwj5b3erhwwt5flxnevr7o FOREIGN KEY (business_id) REFERENCES public.businesses(id);


--
-- Name: businesses fkdh1y7wew1fqwy531d5ojohod5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.businesses
    ADD CONSTRAINT fkdh1y7wew1fqwy531d5ojohod5 FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- Name: qr_codes fkg2udywklhxlu22djttfvugn0u; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qr_codes
    ADD CONSTRAINT fkg2udywklhxlu22djttfvugn0u FOREIGN KEY (business_id) REFERENCES public.businesses(id);


--
-- Name: email_verification_tokens fki1c4mmamlb8keqt74k4lrtwhc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.email_verification_tokens
    ADD CONSTRAINT fki1c4mmamlb8keqt74k4lrtwhc FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: password_reset_tokens fkk3ndxg5xp6v7wd4gjyusp15gq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT fkk3ndxg5xp6v7wd4gjyusp15gq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: catalogs fks2qjj4pv9s86xv0x5yvblpscj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.catalogs
    ADD CONSTRAINT fks2qjj4pv9s86xv0x5yvblpscj FOREIGN KEY (business_id) REFERENCES public.businesses(id);


--
-- PostgreSQL database dump complete
--

\unrestrict 4eBRf49egt96TW32CTpHMvfdoivIJTrHKOsqHTQHbgP0NIU5sP02axFlhPDbecI

