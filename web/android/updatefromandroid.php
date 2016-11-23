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
    
    //Variable utilizada para conocer que informacion nos llega
    $tipo = "tipo";
    
    //Identificador que nos realiza la comprobacion de la actualizacion de los datos
    $response = 0;
    
    //Identificador de la nota o lista si es insertada en el servidor
    $id;
    
    if ( array_key_exists( $tipo, $elementos ) ) {
        
        //Tenemos una actualizacion o un insert
        $id         = $elementos['id'];
        $titulo     = $elementos['titulo'];
        $tipo_el    = $elementos['tipo'];
        $fec_not    = $elementos['fecha_not'];
        $papelera   = $elementos['papelera'];
        $color      = $elementos['color'];
        $orden      = $elementos['orden'];
        
        $cuerpo;
        
        //Tenemos una nota
        if ( $tipo_el == 0 ) {
            
            $cuerpo = $elementos['nota'];
            
            //Debemos de decodificar la imagen para almacenarla en la BBDD
            $imagen = $elementos['imagen'];
            $imagen = base64_decode($imagen);
            $imagen = mysqli_real_escape_string($conn, $imagen);
            
            if ( $id != 0 ) {
                
                //Actualizamos
                
                $query = "update elementos set Titulo = '$titulo', Contenido = '$cuerpo', 
                          Papelera = $papelera, Notificacion = '$fec_not', Color = $color,
                          Orden = $orden where _ID = $id and Email = '$correo'";
                          
                //Debemos de comprobar si la nota tiene imagen o no
                
                //Comprobamos que la nota tenga imagen
                $query_img      = "select BlobType from media where ParentId = $id";
                $res_img        = mysqli_query( $conn, $query_img );
                    
                if ( mysqli_num_rows( $res_img ) > 0 ) {
                        
                    $query_media    = "update media set MimeType = 'image/jpeg', BlobType = '$imagen' where ParentId = $id";
                }
                else
                {
                    $query_media    = "insert into media ( MimeType, BlobType, ParentId ) values ( 'image/jpeg', '$imagen', $id)";
                }
                
                //Actualizamos los elementos
                $res_nota   = mysqli_query( $conn, $query);
                $res_media  = mysqli_query( $conn, $query_media);
                
                if( $res_nota && $res_media ) {
                    
                    $response = 1;
                }
            }
            else
            {
                //Insertamos    
            
                $query = "insert into elementos (Titulo, Contenido, Tipo, Papelera, 
                         Notificacion, Email, Color ) values ( '$titulo', '$cuerpo', $tipo_el, $papelera, 
                         '$fec_not', $correo, $color )";
                      
                //En primer lugar insertamos la nota para poder obtener el _ID asignado
                $res_nota = mysqli_query( $conn, $query );
                
                //Obtenemos el id asignado
                $query_id   = "select _ID from elementos order by _ID desc limit 1";
                $res_id     = mysqli_query($conn, $query_id);
                        
                if ( $res_id ) {
                        
                   $id = mysqli_fetch_array($res_id)[0];
                   $query_id    = "update elementos set Orden = $id where _ID = $id";
                   mysqli_query( $conn, $query_id);
                }
                
                $res_imagen;
                
                if ( !empty( $imagen ) ) {
                
                    $query_media    = "insert into media ( MimeType, BlobType, ParentId ) values ( 'image/jpeg', '$imagen', $id)";
                    $res_imagen     = mysqli_query( $conn, $query_media);
                    
                }
                
                if ( empty($res_imagen) && $res_nota ) {
                    
                    $response = 1;
                }
                elseif( $res_imagen && $res_nota )
                {
                    $response = 1;    
                }
            }
            
        }
        else
        {
            $cuerpo = json_encode($elementos['items']);
            $res;
            
            if ( $id != 0 ) {
                
                //Actualizamos
                $query = "update elementos set Titulo = '$titulo', Contenido = '$cuerpo', 
                          Papelera = $papelera, Notificacion = '$fec_not', Color = $color,
                          Orden = $orden where _ID = $id and Email = $correo";
                          
                //Realizamos la operacion
                $res = mysqli_query($conn, $query);
            }
            else
            {
                //Insertamos
                $query = "insert into elementos (Titulo, Contenido, Tipo, Papelera, 
                         Notificacion, Email, Color ) values ( '$titulo', '$cuerpo', $tipo_el, $papelera, 
                         '$fec_not', $correo, $color )";
                         
                //Realizamos la operacion
                $res = mysqli_query($conn, $query);
                //Obtenemos el id asignado
                $query_id   = "select _ID from elementos order by _ID desc limit 1";
                $res_id     = mysqli_query($conn, $query_id);
                        
                if ( $res_id ) {
                    
                    $id = mysqli_fetch_array($res_id)[0];
                    
                   $query_id    = "update elementos set Orden = $id where _ID = $id";
                   mysqli_query( $conn, $query_id);
                }
            }
            
            
            if ( $res ) $response = 1;
        }
        
        
        //Debemos de generar las actualizaciones necesarias para que el resto de dispositivos conozcan la actualizacion 
        generarActualizacionAndroid ( $conn, $correo, $id, $dispositivo ); 
    }
    else
    {
        //Accion de borrar o recuperar
        $query;
        $update_elemento = 0;
        
        switch($elementos['accion'][0] ) {
            
            case "vaciar" : {
                
                vaciarPapeleraAndroid( $conn, $correo, $dispositivo );
                
                break;
            }
            case "recuperar" : {

                $id                 = $elementos['accion'][1];
                $query              = "update elementos set Papelera = 0 where _ID = $id and Email = $correo";
                $update_elemento    = 1;
                
                //Generamos la actualizacion
                generarActualizacionAndroid ( $conn, $correo, $id, $dispositivo );
                
                break;
            }
            case "papelera" : {
                
                $id                 = $elementos['accion'][1];
                $query              = "update elementos set Papelera = 1 where _ID = $id and Email = $correo";
                $update_elemento    = 1;
                
                 //Generamos la actualizacion
                generarActualizacionAndroid ( $conn, $correo, $id, $dispositivo );
                
                break;
            }
        }
        
            
        $res = mysqli_query( $conn, $query );
        
        if( $update_elemento == 1 && $res ) $response = 1;
    }
    
    
    //Cerramos la conexion
    mysqli_close($conn);
    
    //Respuesta generada tomada por Android
    $array = array('r' => $response, 'id' => $id );
    echo json_encode($array);
?>