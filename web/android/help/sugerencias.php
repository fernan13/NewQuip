<html>
    
    <head>
        
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <h2>Danos tu opinión</h2>
        <p> 
            
            <form action="#" method="post">
                <table border="0">
                    
                    <tr>
                        
                        <td>Nombre: </td><td><input type="text" name="name"/></td>
                        
                    </tr>
                    <tr>
                        
                        <td>Observaciones: </td>
                        <td>
                            
                            <textarea rows="4" cols="20"></textarea>
                            
                        </td>
                        
                    </tr>
                    <tr>
                        
                        <td>
                                
                            <input type="submit" name="submit" value="Enviar"/>
                        
                        </td>
                        
                    </tr>
                    
                </table>        
            </form>
        
        </p>
        
    </body>
</html>

<?php

    if ( isset ( $_POST['submit']) ) {
        
        echo '<span>Gracias por su tiempo :)</span>';
    }
?>