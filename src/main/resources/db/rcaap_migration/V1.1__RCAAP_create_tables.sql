--
-- Name: historic; Type: TABLE; Schema: public;
--

CREATE TABLE IF NOT EXISTS historic (
    id bigint NOT NULL,
    archive_acronym character varying(255) NOT NULL,
    closedaccess integer NOT NULL,
    day integer NOT NULL,
    embargoedaccess integer NOT NULL,
    metadataonlyaccess integer NOT NULL,
    month integer NOT NULL,
    network_id bigint NOT NULL,
    openaccess integer NOT NULL,
    restrictedaccess integer NOT NULL,
    year integer NOT NULL,
    CONSTRAINT historic_pkey PRIMARY KEY (id)
);


--
-- Name: historic_id_seq; Type: SEQUENCE; Schema: public;
--

CREATE SEQUENCE IF NOT EXISTS historic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE historic_id_seq OWNER TO metarepository;

--
-- Name: historic_id_seq; Type: SEQUENCE OWNED BY; Schema: public;
--

ALTER SEQUENCE historic_id_seq OWNED BY historic.id;

--
-- Name: historic id; Type: DEFAULT; Schema: public;
--

ALTER TABLE ONLY historic ALTER COLUMN id SET DEFAULT nextval('historic_id_seq'::regclass);


--
-- Name: project; Type: TABLE; Schema: public;
--

CREATE TABLE IF NOT EXISTS project (
    id bigint NOT NULL,
    dateawarded timestamp without time zone,
    description text,
    enddate timestamp without time zone,
    fundrefuri character varying(255),
    funderacronym character varying(255) NOT NULL,
    fundercountry character varying(255),
    fundername character varying(255),
    fundingamount double precision,
    fundingcurrency character varying(255),
    fundingprogram character varying(255) NOT NULL,
    projectid character varying(255) NOT NULL,
    reference character varying(255),
    startdate timestamp without time zone,
    title character varying(255),
    CONSTRAINT project_pkey PRIMARY KEY (id)
);


--
-- Name: project_id_seq; Type: SEQUENCE; Schema: public;
--

CREATE SEQUENCE IF NOT EXISTS project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE project_id_seq OWNER TO metarepository;

--
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public;
--

ALTER SEQUENCE project_id_seq OWNED BY project.id;


--
-- Name: project id; Type: DEFAULT; Schema: public;
--

ALTER TABLE ONLY project ALTER COLUMN id SET DEFAULT nextval('project_id_seq'::regclass);

