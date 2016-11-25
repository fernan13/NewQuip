<?php 
    
    session_start(); 
    require_once('connectvars.php');
    require_once('functions.php');

?>

<html>

    <head>
        <title>Agregar Nota</title>
        <meta charset="utf-8"/>
    </head>

    <body>
    
        <h1>Agregar Nota</h1>
        <form method="post" action="<?php echo $_SERVER['PHP_SELF']?>" id="form" 
              enctype="multipart/form-data">
        
            <table border ="0">
            
                <tr>
                    <td>Titulo: </td>
                    <td><input type="text" name="titulo" value="<?php if ( isset($_POST['titulo']) ) echo $_POST['titulo']; ?>"/></td>
                </tr>
                <tr>
                
                    <td>Cuerpo: </td>
                    <td><textarea rows="5" cols="50" name="cuerpo" form="form"><?php if ( isset($_POST['cuerpo']) ) echo htmlspecialchars($_POST['cuerpo']); ?></textarea></td>
                </tr>
                <tr>
                    <td>Imagen: </td>
                    <td><input type="file" name="img" accept=".gif,.jpg,.jpeg,.png"/></td>
                </tr>
                <tr>
                    <td><input type="submit" name="submit" value="Agregar"/></td>
                </tr>
            </table>
            
        </form>
    </body>
</html>


<?php 

    if ( isset($_POST['submit']) ) {
        
        $titulo = $_POST['titulo'];
        
        //Funcion para obtener la informacion almacenada en el textarea
        $cuerpo = htmlspecialchars($_POST['cuerpo']);
        $imagen = $_FILES['img']['tmp_name'];
        $correo = $_SESSION['correo'];
        
        //Variable donde se almacena el id generado en la insercion de la nota
        $id_nota;
        
        //Comprobamos los valores
        if ( !empty( $titulo ) && !empty($cuerpo ) ) {
            
            $conn       = mysqli_connect( SERVER, USER, PASS, BD );
            
            $query_el   = "insert into elementos ( Titulo, Contenido, Tipo, Papelera, Notificacion, 
                        Email, Color, Orden, Localizacion ) values ( '$titulo', '$cuerpo', 0, 0, '', $correo, 0, 0, '' )";
            
            $res        = mysqli_query($conn, $query_el);
            
            if ( $res ) {
                
                //Obtenemos el id asignado
                $query_id   = "select _id from elementos order by _id desc limit 1";
                $res_id     = mysqli_query($conn, $query_id);
                    
                if ( $res_id ) {
                        
                    $id_nota    = mysqli_fetch_array($res_id)[0];
                    
                    //Actualizamos el orden
                    $query_up = "update elementos set Orden = $id_nota where _ID = $id_nota";
                    mysqli_query($conn, $query_up);
                }
                
                if ( !empty($imagen) ) {
                    
                        $type       = $_FILES['img']['type'];
                        
                        //FIle nos devuelve en un string el fichero que se le pase como parametro
                        $imagen     = mysqli_real_escape_string( $conn, file_get_contents($imagen));

                        //insertamos en la tabla media la imagen y el tipo
                        $query_media    = "insert into media ( MimeType, BlobType, ParentId ) values ( '$type', '$imagen', $id_nota)";
                        
                        $res_media  = mysqli_query($conn, $query_media);

                        if ( $res_media ) {

                            //Eliminamos la imagen del servidor
                            @unlink($_FILES['img']['tmp_name']);
                        }
                        
                } 
                echo "Insertadp";
                generarActualizacionWeb( $conn, $id_nota, $correo);
                //Tanto si se inserta imagen como si no mandamos al usuario al index
                mysqli_close($conn);
                header('Location: notasuser.php');
            }
        }
        else
        {
            if ( empty($titulo) ) {
                
                echo "Titulo vacio";
            }
            elseif ( empty($cuerpo) ) {
                
                echo "Cuerpo vacio";
            }
        }
    }
?>