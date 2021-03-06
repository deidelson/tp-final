package com.TpFinal.dto;

public class LocalidadRAW {
   private String loc_nombre="";
    private String loc_cpostal;
    private String prv_nombre;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalidadRAW localidad = (LocalidadRAW) o;

        if (loc_nombre != null ? !loc_nombre.equals(localidad.loc_nombre) : localidad.loc_nombre != null) return false;
        if (loc_cpostal != null ? !loc_cpostal.equals(localidad.loc_cpostal) : localidad.loc_cpostal != null) return false;
        return prv_nombre != null ? prv_nombre.equals(localidad.prv_nombre) : localidad.prv_nombre == null;
    }

    @Override
    public int hashCode() {
        int result = loc_nombre != null ? loc_nombre.hashCode() : 0;
        result = 31 * result + (loc_cpostal != null ? loc_cpostal.hashCode() : 0);
        result = 31 * result + (prv_nombre != null ? prv_nombre.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return loc_nombre;
    }

    public LocalidadRAW(String nombre, String codPosta){
        this.loc_nombre=nombre;
        this.loc_cpostal=codPosta;
    }
    public String getNombre() {
        return loc_nombre;
    }

    public void setNombre(String nombre) {
        this.loc_nombre = nombre;
    }

    public String getCodPosta() {
        return loc_cpostal;
    }

    public void setCodPosta(String codPosta) {
        this.loc_cpostal = codPosta;
    }

    public String getProvincia() {
        return prv_nombre;
    }

    public void setProvincia(String provincia) {
        this.prv_nombre = provincia;
    }
}

