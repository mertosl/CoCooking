package com.mertos_l.cocookingfinaldesign;

import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Places;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private Bundle bundle;
    private PagerSlidingTabStrip tabs;
    private CircleIndicator indicator;

    // Recherche
    private RechercheSectionsPagerAdapter rechercheSectionsPagerAdapter;
    private ViewPager rechercheViewPager;
    private ResultatSectionsPagerAdapter resultatSectionsPagerAdapter;
    private ViewPager resultatViewPager;
    private DetailsSectionsPagerAdapter detailsSectionsPagerAdapter;
    private ViewPager detailsViewPager;

    // Profile
    private MonCompteSectionsPagerAdapter moncompteSectionsPagerAdapter;
    private ViewPager moncompteViewPager;
    private EditerCompteSectionsPagerAdapter editercompteSectionsPagerAdapter;
    private ViewPager editerCompteViewPager;

    // Repas
    private RepasSectionsPagerAdapter repasSectionsPagerAdapter;
    private ViewPager repasViewPager;

    // Messagerie
    private MessagerieSectionsPagerAdapter messagerieSectionsPagerAdapter;
    private ViewPager messagerieViewPager;

    // Localisation
    private double latitude;
    private double longitude;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        buildGoogleApiClient();
        initSectionsView(0);
        showRechercheTabs();
    }

    private void initCircleIndicator(ViewPager v, FragmentPagerAdapter f, int ressourceId) {
        ((RelativeLayout)findViewById(R.id.rl_container_edit_profile)).setVisibility(View.VISIBLE);
        ((PagerSlidingTabStrip) findViewById(R.id.tabs)).setVisibility(View.GONE);
        List<Integer> ressourceIds = new ArrayList<Integer>();
        ressourceIds.add(R.id.containerRecherche);
        ressourceIds.add(R.id.containerResultat);
        ressourceIds.add(R.id.containerDetails);
        ressourceIds.add(R.id.containerProfile);
        ressourceIds.add(R.id.containerRepas);
        ressourceIds.add(R.id.containerEditerProfile);
        ressourceIds.add(R.id.containerMessagerie);
        for (int i = 0; i < 7; i++) {
            ((ViewPager) findViewById(ressourceIds.get(i))).setVisibility(ressourceIds.get(i) != ressourceId ? View.GONE : View.VISIBLE);
        }
        v = (ViewPager) findViewById(R.id.containerEditerProfile);
        v.setAdapter(f);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(v);
    }

    private void initPagerSlidingTabStrip(ViewPager v, FragmentPagerAdapter f, int ressourceId) {
        ((RelativeLayout)findViewById(R.id.rl_container_edit_profile)).setVisibility(View.GONE);
        List<Integer> ressourceIds = new ArrayList<Integer>();
        ressourceIds.add(R.id.containerRecherche);
        ressourceIds.add(R.id.containerResultat);
        ressourceIds.add(R.id.containerDetails);
        ressourceIds.add(R.id.containerProfile);
        ressourceIds.add(R.id.containerEditerProfile);
        ressourceIds.add(R.id.containerRepas);
        ressourceIds.add(R.id.containerMessagerie);
        for (int i = 0; i < 7; i++) {
            ((ViewPager) findViewById(ressourceIds.get(i))).setVisibility(ressourceIds.get(i) != ressourceId ? View.GONE : View.VISIBLE);
        }
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.setDividerColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorRouge));
        tabs.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorRouge));
        tabs.setIndicatorColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorRouge));
        tabs.setBackgroundColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorBlanche));
        v = (ViewPager) findViewById(ressourceId);
        v.setAdapter(f);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        v.setPageMargin(pageMargin);
        tabs.setViewPager(v);
    }

    private void initActionBar(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.action_bar_custom);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFBE1C10));
            View view = getSupportActionBar().getCustomView();
            ((TextView)view.findViewById(R.id.title)).setText(title);
        }
    }

    void initSectionsView(int index) {
        initActionBar(index == 0 ? "Recherche" : (index == 1 ? "Profile" : (index == 2 ? "Repas" : "Messagerie")));
        ((TextView) findViewById(R.id.tv_recherche)).setTextColor(ContextCompat.getColor(this.getApplicationContext(), index == 0 ? R.color.colorRouge : R.color.colorBleuMarine));
        ((ImageView) findViewById(R.id.recherche)).setImageDrawable(ContextCompat.getDrawable(this, index == 0 ? R.drawable.ic_recherche_activ : R.drawable.ic_recherche));
        if (index != 0) {
            ((ImageView) findViewById(R.id.recherche)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initSectionsView(0);
                    showRechercheTabs();
                }
            });
        }
        ((TextView) findViewById(R.id.tv_profile)).setTextColor(ContextCompat.getColor(this.getApplicationContext(), index == 1 ? R.color.colorRouge : R.color.colorBleuMarine));
        ((ImageView) findViewById(R.id.profile)).setImageDrawable(ContextCompat.getDrawable(this, index == 1 ? R.drawable.ic_profile_activ : R.drawable.ic_profile));
        if (index != 1) {
            ((ImageView) findViewById(R.id.profile)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initSectionsView(1);
                    showMonCompteTabs();
                }
            });
        }
        ((TextView) findViewById(R.id.tv_repas)).setTextColor(ContextCompat.getColor(this.getApplicationContext(), index == 2 ? R.color.colorRouge : R.color.colorBleuMarine));
        ((ImageView) findViewById(R.id.repas)).setImageDrawable(ContextCompat.getDrawable(this, index == 2 ? R.drawable.ic_repas_activ : R.drawable.ic_repas));
        if (index != 2) {
            ((ImageView) findViewById(R.id.repas)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initSectionsView(2);
                    showRepasTabs();
                }
            });
        }
        ((TextView) findViewById(R.id.tv_messagerie)).setTextColor(ContextCompat.getColor(this.getApplicationContext(), index == 3 ? R.color.colorRouge : R.color.colorBleuMarine));
        ((ImageView) findViewById(R.id.messagerie)).setImageDrawable(ContextCompat.getDrawable(this, index == 3 ? R.drawable.ic_messagerie_activ : R.drawable.ic_messagerie));
        if (index != 3) {
            ((ImageView) findViewById(R.id.messagerie)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initSectionsView(3);
                    showMessagerieTabs();
                }
            });
        }
    }

    public void showRepasTabs() {
        repasSectionsPagerAdapter = new RepasSectionsPagerAdapter(getSupportFragmentManager());
        initPagerSlidingTabStrip(repasViewPager, repasSectionsPagerAdapter, R.id.containerRepas);
    }

    public void showRechercheTabs() {
        rechercheSectionsPagerAdapter = new RechercheSectionsPagerAdapter(getSupportFragmentManager());
        initPagerSlidingTabStrip(rechercheViewPager, rechercheSectionsPagerAdapter, R.id.containerRecherche);
    }

    public void showResultatTabs(String address, String date, String words) {
        resultatSectionsPagerAdapter = new ResultatSectionsPagerAdapter(address, date, words, getSupportFragmentManager());
        initPagerSlidingTabStrip(resultatViewPager, resultatSectionsPagerAdapter, R.id.containerResultat);
    }

    public void showDetailsTabs(String mealid, String host) {
        detailsSectionsPagerAdapter = new DetailsSectionsPagerAdapter(mealid, host, getSupportFragmentManager());
        initPagerSlidingTabStrip(detailsViewPager, detailsSectionsPagerAdapter, R.id.containerDetails);
    }

    public void showMonCompteTabs() {
        moncompteSectionsPagerAdapter = new MonCompteSectionsPagerAdapter(getSupportFragmentManager());
        initPagerSlidingTabStrip(moncompteViewPager, moncompteSectionsPagerAdapter, R.id.containerProfile);
    }

    public void showEditerCompteTabs() {
        editercompteSectionsPagerAdapter = new EditerCompteSectionsPagerAdapter(getSupportFragmentManager());
        initCircleIndicator(editerCompteViewPager, editercompteSectionsPagerAdapter, R.id.containerEditerProfile);
    }

    public void showMessagerieTabs() {
        messagerieSectionsPagerAdapter = new MessagerieSectionsPagerAdapter(getSupportFragmentManager());
        initPagerSlidingTabStrip(messagerieViewPager, messagerieSectionsPagerAdapter, R.id.containerMessagerie);
    }

    public class RechercheSectionsPagerAdapter extends FragmentPagerAdapter {
        RechercheSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RechercheFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Formulaire de recherche";
        }
    }

    public class ResultatSectionsPagerAdapter extends FragmentPagerAdapter {
        String address;
        String date;
        String words;

        ResultatSectionsPagerAdapter(String address, String date, String words, FragmentManager fm) {
            super(fm);
            this.address = address;
            this.date = date;
            this.words = words;
        }

        @Override
        public Fragment getItem(int position) {
            return ResultatRechercheFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), address, date, words);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Liste des résultats";
        }
    }

    public class DetailsSectionsPagerAdapter extends FragmentPagerAdapter {
        String mealid;
        String host;

        DetailsSectionsPagerAdapter(String mealid, String host, FragmentManager fm) {
            super(fm);
            this.mealid = mealid;
            this.host = host;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DetailsRepasFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), mealid, host);
                case 1:
                    return ProfilFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), host, 1);
                case 2:
                    return QuestionRepasFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), mealid, host);
            }
            return DetailsRepasFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), mealid, host);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Repas";
                case 1:
                    return "Cuisto";
                case 2:
                    return "Questions";
            }
            return null;
        }
    }

    public class RepasSectionsPagerAdapter extends FragmentPagerAdapter {
        RepasSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CreationFragment.newInstance(getIntent().getExtras().getString("uid"), getIntent().getExtras().getString("api_key"));
                case 1:
                    return ParticipationsFragment.newInstance(getIntent().getExtras().getString("uid"), getIntent().getExtras().getString("api_key"));
                case 2:
                    return RepasFragment.newInstance(getIntent().getExtras().getString("uid"), getIntent().getExtras().getString("api_key"));
            }
            return CreationFragment.newInstance(getIntent().getExtras().getString("uid"), getIntent().getExtras().getString("api_key"));
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Création";
                case 1:
                    return "CoCooks";
                case 2:
                    return "Repas";
            }
            return null;
        }
    }

    public class MonCompteSectionsPagerAdapter extends FragmentPagerAdapter {
        MonCompteSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProfilFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), bundle.getString("userid"), 0);
                case 1:
                    return PreferenceFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
            }
            return ProfilFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), bundle.getString("userid"), 0);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profil";
                case 1:
                    return "Préférences";
            }
            return null;
        }
    }

    public class EditerCompteSectionsPagerAdapter extends FragmentPagerAdapter {
        EditerCompteSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProfilPersonnelFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
                case 1:
                    return ProfilSecuriteFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"), bundle.getString("email"));
                case 2:
                    return ProfilLocalisationFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
                case 3:
                    return ProfilComplementaireFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
            }
            return ProfilPersonnelFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Information personnelle";
                case 1:
                    return "Sécurité";
                case 2:
                    return "Localisation";
                case 3:
                    return "Information complémentaire";
            }
            return null;
        }
    }

    public class MessagerieSectionsPagerAdapter extends FragmentPagerAdapter {
        public MessagerieSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return QuestionFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
                case 1:
                    return ReponseFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
            }
            return QuestionFragment.newInstance(bundle.getString("uid"), bundle.getString("api_key"));
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Vos questions";
                case 1:
                    return "Vos réponses";
            }
            return null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Double> getLocation(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        List<Double> latlng = new ArrayList<Double>();

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            latlng.add((double) (location.getLatitude()));
            latlng.add((double) (location.getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latlng;
    }

    public String getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public List<Double> getCurrentLocation() {
        List<Double> latlng = new ArrayList<Double>();
        JSONObject jsonObject1 = new JSONObject();

        try {
            jsonObject1.put("", "");
            LocationRequestTask locationRequestTask = new LocationRequestTask(jsonObject1.toString());
            JSONObject jsonObject2 = locationRequestTask.execute().get();
            if (!jsonObject2.has("error")) {
                JSONObject jsonObject3 = new JSONObject(jsonObject2.getString("location"));
                latlng.add(jsonObject3.getDouble("lat"));
                latlng.add(jsonObject3.getDouble("lng"));
                latitude = latlng.get(0);
                longitude = latlng.get(1);
            } else {
                return null;
            }
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return latlng;
    }

    public boolean isFeatureAvailable(String feature) {
        final PackageManager packageManager = getApplicationContext().getPackageManager();
        final FeatureInfo[] featuresList = packageManager.getSystemAvailableFeatures();
        for (FeatureInfo f : featuresList) {
            if (f.name != null && f.name.equals(feature)) {
                return true;
            }
        }
        return false;
    }

    public File bitmapToFile(Bitmap _bitmap, String s) throws IOException {
        //create a file to write bitmap data
        File f = new File(this.getCacheDir(), s + ".jpeg");
        f.createNewFile();
        //Convert bitmap to byte array
        Bitmap bitmap = _bitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

    public final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return getResources().getColor(id);
        }
    }
}
