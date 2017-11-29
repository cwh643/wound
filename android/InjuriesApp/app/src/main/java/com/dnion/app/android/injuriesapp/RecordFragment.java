package com.dnion.app.android.injuriesapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment extends Fragment {

    public static final String TAG = "record_fragment";

    private MainActivity mActivity;

    private Fragment currentFragment;

    private View rootView;

    public static RecordFragment createInstance() {
        RecordFragment fragment = new RecordFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.record_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        final Button btn_record1 = (Button)rootView.findViewById(R.id.btn_record1);
        btn_record1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment1) {
                    return;
                }
                RecordFragment1 fragment = RecordFragment1.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record2 = (Button)rootView.findViewById(R.id.btn_record2);
        btn_record2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment2) {
                    return;
                }
                RecordFragment2 fragment = RecordFragment2.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record3 = (Button)rootView.findViewById(R.id.btn_record3);
        btn_record3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment3) {
                    return;
                }
                RecordFragment3 fragment = RecordFragment3.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record4 = (Button)rootView.findViewById(R.id.btn_record4);
        btn_record4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment4) {
                    return;
                }
                RecordFragment4 fragment = RecordFragment4.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record5 = (Button)rootView.findViewById(R.id.btn_record5);
        btn_record5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment5) {
                    return;
                }
                RecordFragment5 fragment = RecordFragment5.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record6 = (Button)rootView.findViewById(R.id.btn_record6);
        btn_record6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment6) {
                    return;
                }
                RecordFragment6 fragment = RecordFragment6.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record7 = (Button)rootView.findViewById(R.id.btn_record7);
        btn_record7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment7) {
                    return;
                }
                RecordFragment7 fragment = RecordFragment7.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record8 = (Button)rootView.findViewById(R.id.btn_record8);
        btn_record8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment8) {
                    return;
                }
                RecordFragment8 fragment = RecordFragment8.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });
        final Button btn_record9 = (Button)rootView.findViewById(R.id.btn_record9);
        btn_record9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment9) {
                    return;
                }
                RecordFragment9 fragment = RecordFragment9.createInstance();
                currentFragment = fragment;
                selectButton(v);
                startFragment(fragment);
            }
        });

        ImageButton btn_up = (ImageButton)rootView.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null) {
                    if (currentFragment instanceof RecordFragment8) {
                        RecordFragment7 fragment = RecordFragment7.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record7);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment7) {
                        RecordFragment6 fragment = RecordFragment6.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record6);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment6) {
                        RecordFragment5 fragment = RecordFragment5.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record5);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment5) {
                        RecordFragment4 fragment = RecordFragment4.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record4);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment4) {
                        RecordFragment3 fragment = RecordFragment3.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record3);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment3) {
                        RecordFragment2 fragment = RecordFragment2.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record2);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment2) {
                        RecordFragment9 fragment = RecordFragment9.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record9);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment9) {
                        RecordFragment1 fragment = RecordFragment1.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record1);
                        startFragment(fragment);
                    }
                }
            }
        });

        ImageButton btn_down = (ImageButton)rootView.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null) {
                    if (currentFragment instanceof RecordFragment1) {
                        RecordFragment9 fragment = RecordFragment9.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record9);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment9) {
                        RecordFragment2 fragment = RecordFragment2.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record2);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment2) {
                        RecordFragment3 fragment = RecordFragment3.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record3);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment3) {
                        RecordFragment4 fragment = RecordFragment4.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record4);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment4) {
                        RecordFragment5 fragment = RecordFragment5.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record5);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment5) {
                        RecordFragment6 fragment = RecordFragment6.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record6);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment6) {
                        RecordFragment7 fragment = RecordFragment7.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record7);
                        startFragment(fragment);
                    } else if (currentFragment instanceof RecordFragment7) {
                        RecordFragment8 fragment = RecordFragment8.createInstance();
                        currentFragment = fragment;
                        selectButton(btn_record8);
                        startFragment(fragment);
                    }
                }
            }
        });


        RecordFragment1 fragment = RecordFragment1.createInstance();
        currentFragment = fragment;
        startFragment(fragment);
    }

    private void selectButton(View view) {
        ViewGroup recordGroup = (ViewGroup)rootView.findViewById(R.id.btn_record_group);
        int count = recordGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            Button bth = (Button)recordGroup.getChildAt(i);
            bth.setBackgroundColor(Color.TRANSPARENT);
            bth.setTextColor(Color.WHITE);
        }
        Button selectButton = (Button)view;
        selectButton.setBackgroundColor(Color.WHITE);
        selectButton.setTextColor(Color.rgb(182,182,182));
    }

    private void startFragment(Fragment fragment) {
        String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
        if (inpatientNo == null || inpatientNo.length() == 0) {
            ToastUtil.showLongToast(mActivity, "先填写患者基本信息");
            return;
        }
        mActivity.saveRecordInfo();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.record_container, fragment)
                .commit();
    }
}
