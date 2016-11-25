<?php

    function getIdFromCorreo( &$conn, $correo ) {
        
        $query  = "select id from usuarios where email = '$correo'";
        $res    = mysqli_query( $conn, $query );
        
        $id;
        
        if ( $res ) {
            
            $id = mysqli_fetch_array($res)[0];    
        }
        
        return $id;
    }
    
    function registrarUsuario ( &$conn, $correo, $dispositivo ) {
        
        if ( !empty($correo) ) {
            
            $query      = "select id from usuarios where email = '$correo'";
            $res        = mysqli_query( $conn, $query );
            
            //Si el usuario no existe lo creamos
            if ( mysqli_num_rows($res) == 0 ) {
                
                $query_insert = "insert into usuarios (email) values ('$correo')";
                mysqli_query($conn, $query_insert);
                
            }
            
            //Registramos su dispositivo
            $query_dispositivo  = "select id from dispositivos where idDispositivo = '$dispositivo'";
            $res_dispositivo    = mysqli_query( $conn, $query_dispositivo );
                
            //obtenemos el id del correo
            $correoId           = getIdFromCorreo( $conn, $correo );
            
            if ( mysqli_num_rows($res_dispositivo) == 0 ) {
                    
                //Si no existe el dispositivo lo registramos
                $query =    "insert into dispositivos ( idDispositivo, idCorreo ) 
                            values ( '$dispositivo', $correoId)";
                                
                mysqli_query( $conn, $query);            
            }
            
            //Debemos de generar las actualizaciones para que el dispositivo que se conecte sepa que debe insertar
            $query_update   = "select _ID from elementos where Email = $correoId";
            $res_update     = mysqli_query( $conn, $query_update );
            
            if ( $res_update ) {
                
                while ( $row = mysqli_fetch_array( $res_update ) ) {
                    
                    //Generamos las actualizaciones
                    $query = "insert into actualizar ( idElemento, idDispositivo ) values ( $row[0], '$dispositivo')";
                    mysqli_query( $conn, $query );
                }
            }
                
                
        }
        
    }
    
    //Metodo que comprueba que la actualizacion exista
    function comprobarActualizacion ( &$conn, $id, $dispositivo ) {
        
        $query  = "select id from actualizar where idElemento = $id AND idDispositivo = '$dispositivo'";
        $res    = mysqli_query( $conn, $query );
        
        return mysqli_num_rows($res) == 0;
    }
    
    //Metodo que comprueba
    
    function generarActualizacionWeb ( &$conn, $id, $correo ) {
        
       //Debemos de insertar en la tabla actualizaciones que se ha añadido un nuevo registro
        $query_dispositivo  = "select idDispositivo from dispositivos where idCorreo = $correo";
        $res_dispositivo    = mysqli_query( $conn, $query_dispositivo );
        
        if ( $res_dispositivo ) {
            
            while( $row_dispositivo = mysqli_fetch_array( $res_dispositivo ) ) {
                      
                if ( comprobarActualizacion( $conn, $id, $row_dispositivo[0] ) ) {
                        
                    $query_actualizar   =   "insert into actualizar (idElemento, 
                                             idDispositivo ) values ( $id, '$row_dispositivo[0]')";
                            
                    mysqli_query( $conn, $query_actualizar);
                        
                }                    
            }    
        }
          
                
    }
    
    function generarActualizacionAndroid ( &$conn, $idCorreo, $idElemento, $idDispositivo ) {
        
        //Obtenemos todos los dispositivos menos el que realiza la actualizacion
        $query_info =   "select idDispositivo from dispositivos where idCorreo = $idCorreo 
                        AND idDispositivo != '$idDispositivo'";
        
        $res_info   =   mysqli_query( $conn, $query_info );
        
        if ( $res_info ) {
            
            while ( $row = mysqli_fetch_array( $res_info ) ) {
                
                if ( comprobarActualizacion( $conn, $idElemento, $row[0] ) ) {
                        
                        $query_actualizar   =   "insert into actualizar (idElemento, 
                                                idDispositivo ) values ( $idElemento, '$row[0]')";
                            
                        mysqli_query( $conn, $query_actualizar);
                        
                    }
            }
        }
    }
    
    function eliminarElementoWeb ( &$conn, $id, $correo ) {
        
        //Debemos de insertar en la tabla actualizaciones que se ha añadido un nuevo registro
        $query_dispositivo  = "select idDispositivo from dispositivos where idCorreo = $correo";
        $res_dispositivo    = mysqli_query( $conn, $query_dispositivo );
                
        if ( $res_dispositivo ) {
            
            
            //Por ultimo eliminamos el elemento
            //Debido a la clausula on delete cascade no es necesario eliminar elementos de la tabla media ni de actualizar
            $query_el   = "delete from elementos where _ID = $id";
            mysqli_query( $conn, $query_el );    
            
            $query_eliminar = "delete from actualizar where idDispositivo = '$row_dispositivo[0]'";
            
            while( $row_dispositivo = mysqli_fetch_array( $res_dispositivo ) ) {
                          
                    $query_info         =   "insert into borrados ( idElemento, idDispositivo ) 
                                            values ( $id, '$row_dispositivo[0]')";
                    $res_info           =   mysqli_query( $conn, $query_info );
            }
            
        }
        
    }
    
    function vaciarPapeleraAndroid ( &$conn, $correo, $dispositivo ) {
        
        $query_info = "select _ID from elementos where Papelera = 1 AND Email = $correo";
        $res_info   = mysqli_query( $conn, $query_info );
        
        if ( $res_info ) {
            
            while ( $row = mysqli_fetch_array( $res_info ) ) {
                
                //Eliminamos la actualizacion
                $query_actualizar = "delete from actualizar where idElemento = $row[0]";
                mysqli_query( $conn, $query_actualizar );
                
                //Debemos de eliminar las actualizaciones de los demas dispositivos y agregarlos a la tabla borrados
                $query  = "select idDispositivo from dispositivos where idCorreo = $correo AND idDispositivo <> '$dispositivo'";
                $res    = mysqli_query( $conn, $query );
                
                if ( $res ) {
                    
                    while ( $row_dis = mysqli_fetch_array ( $res ) ) {
                        
                        $query_borrados = "insert into borrados ( idElemento, idDispositivo ) values ( $row[0], '$row_dis[0]')";
                        mysqli_query( $conn, $query_borrados );
                    }
                }
                
                //Eliminamos el elemento
                $query_del = "delete from elementos where _ID = $row[0]";
                mysqli_query( $conn, $query_del );
            }
        }
    }
    
    function eliminarActualizacionDispositivo ( &$conn, $id, $idDispositivo ) {
        
        $query_actualizar   =   "delete from actualizar where idElemento = $id 
                                AND idDispositivo = '$idDispositivo'";
        $query_eliminar     =   "delete from borrados where idElemento = $id 
                                AND idDispositivo = '$idDispositivo'";
        
        mysqli_query( $conn, $query_actualizar );
        mysqli_query( $conn, $query_eliminar );
    }
    

?>