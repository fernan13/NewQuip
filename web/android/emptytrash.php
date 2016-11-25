<?php

    require_once('../connectvars.php');
    require_once('../functions.php');
    
    
    //Conexion con la BBDD
    $conn = mysqli_connect( SERVER, USER, PASS, BD );
    
    //Obtenemos la informacion desde el flujo abierto en Android
    $json = file_get_contents('php://input');
    
    //Obtenemos nuestro array de elementos
    $elementos = json_decode($json);
    //Se convierte en un array normal
    $elementos = (array)$elementos;
    
    //Obtenemos el correo del usuario y buscamos su id en la bbdd
    $correo         = getIdFromCorreo( $conn, $elementos['email'] );
    $dispositivo    = $elementos['idDispositivo'];
    
    $ids    = $elementos['accion'];
    
    if ( !empty( $ids ) ) {
        
        foreach ( $ids as $id ) {
            
            //En primer lugar debemos de eliminar de la tabla actualizaciones
            $query_act  = "delete from actualizar where idElemento = $id";
            $res_act    = mysqli_query( $conn, $query_act );
            
            if ( $res_act ) {
                
                //Insertamos en la tabla borrados
                $query_dis  = "select idDispositivo from dispositivos where idDispositivo <> '$dispositivo'";
                $res_dis    = mysqli_query( $conn, $query_dis );
                
                if ( $res_dis ) {
                    
                    while ( $row = mysqli_fetch_array( $res_dis ) ) {
                        
                        $query_in   = "insert into borrados ( idElemento, idDispositivo ) values ( $id, $row[0])";
                        mysqli_query( $conn, $query_in );
                    }
                }
                
            }
        }
    }

    echo '{ "r" : 2 }';
?>