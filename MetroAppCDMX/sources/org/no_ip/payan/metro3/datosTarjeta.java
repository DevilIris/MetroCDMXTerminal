package org.no_ip.payan.metro3;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.app.AppCompatActivity;
import android.support.p003v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.ByteBuffer;
import java.util.Locale;
import org.no_ip.payan.metro3.clases.C0372util;
import org.no_ip.payan.metro3.clases.MCReader;
import org.no_ip.payan.metro3.clases.Tarjeta;

public class datosTarjeta extends AppCompatActivity {
    int escribirEn = 0;
    TextView lblSaldo;

    /* access modifiers changed from: package-private */
    public void setSaldo(double cd) {
        if (Tarjeta.tipo == 0) {
            String vb = C0372util.byte2HexString(ByteBuffer.allocate(4).putInt(Integer.reverseBytes((int) cd)).array());
            String vbInverted = C0372util.byte2HexString(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(((int) cd) ^ -1)).array());
            String addrInverted = Integer.toHexString(Integer.parseInt("02", 16) ^ -1).toUpperCase(Locale.getDefault()).substring(6, 8);
            String salida = vb + vbInverted + vb + "02" + addrInverted + "02" + addrInverted;
            Tag t = C0372util.getTag();
            if (t == null) {
                Toast.makeText(this, "NO HAY UNA TARJETA", 1);
                return;
            }
            MCReader algo = MCReader.get(t);
            algo.close();
            algo.writeBlock(2, this.escribirEn, C0372util.hexStringToByteArray(salida), C0372util.hexStringToByteArray("B3F3A0C5A1CC"), true);
            Intent i = new Intent(this, leerTarjeta.class);
            algo.close();
            startActivity(i);
            return;
        }
        if (Tarjeta.tipo == 1) {
        }
    }

    /* access modifiers changed from: package-private */
    public double getSaldo() {
        long p1;
        long p2;
        char xd;
        char xd2;
        if (Tarjeta.tipo == 0) {
            String pt1 = "";
            String pt2 = "";
            int i = 0;
            while (i < 16 && (xd2 = Tarjeta.sectores[6][0].charAt(i)) != '0') {
                pt1 = pt1 + xd2;
                i++;
            }
            int i2 = 0;
            while (i2 < 16 && (xd = Tarjeta.sectores[6][1].charAt(i2)) != '0') {
                pt2 = pt2 + xd;
                i2++;
            }
            try {
                p1 = Long.parseLong(pt1, 16);
            } catch (Exception e) {
                p1 = 0;
            }
            try {
                p2 = Long.parseLong(pt2, 16);
            } catch (Exception e2) {
                p2 = 0;
            }
            if (p1 < p2) {
                double sal = ((double) Integer.reverseBytes(ByteBuffer.wrap(C0372util.hexStringToByteArray(Tarjeta.sectores[2][1].substring(0, 8))).getInt())) / 100.0d;
                this.escribirEn = 1;
                return sal;
            }
            double sal2 = ((double) Integer.reverseBytes(ByteBuffer.wrap(C0372util.hexStringToByteArray(Tarjeta.sectores[2][0].substring(0, 8))).getInt())) / 100.0d;
            this.escribirEn = 0;
            return sal2;
        }
        if (Tarjeta.tipo == 1) {
        }
        return 0.0d;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0368R.layout.activity_datos_tarjeta);
        setSupportActionBar((Toolbar) findViewById(C0368R.C0370id.toolbar));
        ImageView im = (ImageView) findViewById(C0368R.C0370id.imageView);
        if (Tarjeta.tipo == 1) {
            im.setImageResource(C0368R.C0369drawable.suburbano);
        }
        setTitle("TARJETA " + C0372util.byte2HexString(C0372util.getTag().getId()));
        ((FloatingActionButton) findViewById(C0368R.C0370id.fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(datosTarjeta.this);
                builder.setTitle((CharSequence) "INGRESE LA CANTIDAD DE SALDO PARA LA TARJETA");
                final EditText input = new EditText(datosTarjeta.this);
                input.setInputType(2);
                builder.setView((View) input);
                builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        try {
                            double cantidad = Double.parseDouble(m_Text);
                            if (m_Text.length() > 4) {
                                Integer.parseInt("ERROR");
                            }
                            if (cantidad > 99.99d) {
                                Integer.parseInt("ERROR");
                            }
                            datosTarjeta.this.setSaldo(cantidad * 100.0d);
                        } catch (Exception e) {
                            Toast.makeText(datosTarjeta.this, "NUMERO INVALIDO", 1);
                        }
                    }
                });
                builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        this.lblSaldo = (TextView) findViewById(C0368R.C0370id.lblSaldo);
        this.lblSaldo.setText("$" + getSaldo() + " MXN");
    }
}
