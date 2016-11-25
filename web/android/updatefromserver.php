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
    $correo         = getIdFromCorreo( $conn, $elementos['email']);
    $dispositivo    = $elementos['idDispositivo'];
    $accion         = $elementos['accion'];
    
    if ( !empty($accion) || count($accion) > 0 ) {
        
        if ( $accion == "actualizar") 
        {
            
            //Debemos de enviar toda la informacion actualizada en el servidor web
            $query_update   =   "select elementos._id, elementos.Titulo, elementos.Contenido, elementos.Tipo, 
                                elementos.Papelera, elementos.Notificacion, elementos.Color, elementos.Orden, 
                                elementos.Localizacion, media.BlobType from elementos left join media on 
                                elementos._ID = media.ParentId inner join actualizar 
                                on elementos._ID = actualizar.idElemento 
                                where Email = $correo AND idDispositivo = '$dispositivo'";
                                
            $res_update     =   mysqli_query( $conn, $query_update );
                
                
            $query_del  = "select idElemento from borrados where idDispositivo = '$dispositivo'";
            $res_del    = mysqli_query( $conn, $query_del );
                    
            if ( $res_update || $res_del ) {
                    
                $info = array();
                    
                $actualizar = array();
                $eliminar   = array();
                
                if ( $res_update ) {
                        
                    while ( $row = mysqli_fetch_array($res_update) ) {
                        
                        $keys = array();
                            
                        foreach ( array_keys($row) as $indice ) {
                                
                            if ( !is_numeric($indice) ) $keys[] =  $indice; 
                        }
                        
                        $valores = array();
                          
                        foreach ( $keys as $key ) {
                                
                            $valor = $row[$key];
                                
                            
                            switch ( $key ) {
                                    
                                case "Contenido" : {
                                        
                                    $contenido_lista = json_decode($row[$key]);
                                    
                                    if ( is_array( $contenido_lista) ) {
                                            
                                        $valor = $contenido_lista;
                                    }
                                    
                                    break;
                                }
                                    
                                case "BlobType" : {
                                        
                                    if (!empty($valor) ) $valor = base64_encode($valor);
                                    break;
                                }
                            }
                                
                            $valores[$key] = $valor;
                        }
                            
                        $actualizar[] = $valores;
                    }
                
                }
                else
                {
                    $actualizar = "";    
                }
                
                if (  $res_del ){
                
                    $valores = array();
                    
                    while ( $row = mysqli_fetch_array($res_del) ) {
                        
                        $valores[] = $row[0];
                    }
                    
                    $eliminar = $valores;
                }
                else
                {
                    $eliminar = "";        
                }
                
                if ( !empty($actualizar))   $info["actualizar"] = $actualizar;
                if ( !empty($eliminar))     $info["eliminar"]   = $eliminar;
                    
                echo json_encode($info);
                    
            }
            else
            {
                echo '{ "r" : 3 }';
            }
                
        }
        else
        {   
            //Aqui debemos de eliminar los registros de las tablas borrados y actualizar
            foreach ( $accion as $id ) {
                
                eliminarActualizacionDispositivo( $conn, $id, $dispositivo );
            }
            
            echo '{ "r" : 3 }';
        }
            
        
        mysqli_close( $conn );
    } 
    
    


?>