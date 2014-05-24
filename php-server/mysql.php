<?php

define("SQL_HOST","localhost");
define("SQL_DBNAME","tridni_net_main");
define("SQL_USERNAME","tridni_net_main");
define("SQL_PASSWORD","ondra");

mysql_connect(SQL_HOST, SQL_USERNAME, SQL_PASSWORD);

mysql_set_charset("utf8");

mysql_select_db(SQL_DBNAME);

mysql_query(
	"SET NAMES 'utf8' COLLATE 'utf8_general_ci';".
	"SET CHARACTER SET utf8;".
	"SET character_set_client = utf8;".
	"SET character_set_connection = utf8;".
	"SET character_set_results = utf8;"
);

?>