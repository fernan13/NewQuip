<?php

    require_once('../connectvars.php');
    require_once('../functions.php');
    
    
    //Conexion con la BBDD
    $conn = mysqli_connect( SERVER, USER, PASS, BD );
    
    //Obtenemos la informacion desde el flujo abierto en Android
    $json = file_get_contents('php://input');
    //Obtenemos nuestro array de elementos
    $elementos  = json_decode($json);
    //Se convierte en un array normal
    $elementos  = (array)$elementos;
    
    $correo         = $elementos['email'];
    $dispositivo    = $elementos['idDispositivo'];
    
    registrarUsuario ( $conn, $correo, $dispositivo );
   
?>