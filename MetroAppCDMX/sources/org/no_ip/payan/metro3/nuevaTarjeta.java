package org.no_ip.payan.metro3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.p003v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.no_ip.payan.metro3.clases.C0372util;
import org.no_ip.payan.metro3.clases.Clave;
import org.no_ip.payan.metro3.clases.MCReader;
import org.no_ip.payan.metro3.clases.Tarjeta;

public class nuevaTarjeta extends AppCompatActivity {
    MCReader algo;
    int cual = 0;
    TextView lblEstado;

    /* renamed from: pb */
    ProgressBar f31pb;

    private class escribir extends AsyncTask<Integer, Void, Integer> {
        private escribir() {
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Integer... params) {
            for (int i = 0; i < 4; i++) {
                if (params[0].intValue() != 0 || i != 0) {
                    nuevaTarjeta.this.algo.close();
                    if (nuevaTarjeta.this.cual == 0) {
                        nuevaTarjeta.this.algo.writeBlock(params[0].intValue(), i, C0372util.hexStringToByteArray(Clave.dumpMetro[params[0].intValue()][i]), C0372util.hexStringToByteArray("FFFFFFFFFFFF"), true);
                    } else {
                        nuevaTarjeta.this.algo.writeBlock(params[0].intValue(), i, C0372util.hexStringToByteArray(Clave.dumpSuburbano[params[0].intValue()][i]), C0372util.hexStringToByteArray("FFFFFFFFFFFF"), true);
                    }
                }
            }
            return params[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer returno) {
            if (returno.intValue() + 1 <= 15) {
                Integer returno2 = Integer.valueOf(returno.intValue() + 1);
                nuevaTarjeta.this.lblEstado.setText("ESCRIBIENDO\n SECTOR " + returno2);
                nuevaTarjeta.this.f31pb.setProgress((returno2.intValue() * 100) / 15);
                new escribir().execute(new Integer[]{returno2});
                return;
            }
            nuevaTarjeta.this.algo.close();
            nuevaTarjeta.this.startActivity(new Intent(nuevaTarjeta.this, leerTarjeta.class));
            nuevaTarjeta.this.finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0368R.layout.activity_nueva_tarjeta);
        setTitle("ESCRIBIENDO TARJETA");
        this.lblEstado = (TextView) findViewById(C0368R.C0370id.lblEstadoEscritura);
        this.f31pb = (ProgressBar) findViewById(C0368R.C0370id.progressBar);
        this.cual = getIntent().getExtras().getInt("cual");
        System.out.println("MIRAME " + this.cual);
        Tarjeta.tipo = this.cual;
        try {
            this.algo = MCReader.get(C0372util.getTag());
        } catch (Exception e) {
            Toast.makeText(this, "ERROR AL PROCESAR LA TARJETA", 1);
        }
        new escribir().execute(new Integer[]{0});
    }
}
