/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, Select, Row, Col, message } from "antd";
import { reqUserInfo } from "@/api/user";
import { getTahunAjaran } from "@/api/tahun-ajaran";
import { getSemester } from "@/api/semester";
import { getKelas } from "@/api/kelas";
import { getSeason } from "@/api/season";
import { getSoalUjian } from "@/api/soalUjian";
import { getSubjects } from "@/api/subject";
import { getRPSDetail } from "@/api/rpsDetail";

const { Option } = Select;

const EditBankSoalForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
}) => {
  const [form] = Form.useForm();

  const [userSchoolId, setUserSchoolId] = useState([]);
  const [subjectList, setSubjectList] = useState([]);
  const [rpsDetailList, setRpsDetailList] = useState([]);
  const [tahunAjaranList, setTahunAjaranList] = useState([]);
  const [semesterList, setSemesterList] = useState([]);
  const [kelasList, setKelasList] = useState([]);
  const [seasonList, setSeasonList] = useState([]);
  const [filteredSeasonList, setFilteredSeasonList] = useState([]);
  const [creatorInfo, setCreatorInfo] = useState(null);

  const fetchUserInfo = async () => {
    try {
      const response = await reqUserInfo();
      setUserSchoolId(response.data.school_id);
    } catch (error) {
      message.error("Gagal mengambil informasi pengguna");
    }
  };

  const fetchSubjectList = async () => {
    try {
      const result = await getSubjects();
      if (result.data.statusCode === 200) {
        setSubjectList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchRpsDetailList = async () => {
    try {
      const result = await getRPSDetail();
      if (result.data.statusCode === 200) {
        setRpsDetailList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchKelasList = async () => {
    try {
      const result = await getKelas();
      if (result.data.statusCode === 200) {
        setKelasList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchSeasonList = async () => {
    try {
      const result = await getSeason();
      if (result.data.statusCode === 200) {
        setSeasonList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const filterSeasonOptions = (semesterId, kelasId) => {
    const filtered = (seasonList || []).filter((item) => {
      const matchesSemester = !semesterId || item?.semester?.idSemester === semesterId;
      const matchesKelas = !kelasId || item?.kelas?.idKelas === kelasId;
      return matchesSemester && matchesKelas;
    });

    setFilteredSeasonList(filtered);
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        await fetchUserInfo();
        await fetchSubjectList();
        await fetchRpsDetailList();
        await fetchKelasList();
        await fetchSeasonList();

        const [tahunAjaranRes, semesterRes] = await Promise.all([
          getTahunAjaran(),
          getSemester(),
        ]);

        setTahunAjaranList(tahunAjaranRes.data.content || []);
        setSemesterList(semesterRes.data.content || []);
      } catch (error) {
        message.error("Gagal memuat data");
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchCreatorInfo = async () => {
      if (currentRowData?.idSoalUjian) {
        try {
          const result = await getSoalUjian();
          if (result.data.statusCode === 200) {
            const soalUjian = result.data.content.find(
              (item) => item.idSoalUjian === currentRowData.idSoalUjian
            );
            if (soalUjian && soalUjian.createdBy) {
              setCreatorInfo(soalUjian.createdBy);
            }
          }
        } catch (error) {
          console.error("Gagal mengambil info pembuat:", error);
        }
      }
    };

    fetchCreatorInfo();
  }, [currentRowData?.idSoalUjian]);

  useEffect(() => {
    if (currentRowData) {
      form.setFieldsValue({
        idBankSoal: currentRowData.idBankSoal,
        idStudyProgram: currentRowData.study_program?.id || currentRowData.school?.idSchool || userSchoolId,
        idSubject: currentRowData.subject?.id,
        idRpsDetail: currentRowData.rps_detail?.id,
        idTahun: currentRowData.tahunAjaran?.idTahun,
        idSemester: currentRowData.semester?.idSemester,
        idKelas: currentRowData.kelas?.idKelas,
        idSeason: currentRowData.seasons?.idSeason,
        idSchool: currentRowData.school?.idSchool,
      });

      filterSeasonOptions(
        currentRowData.semester?.idSemester,
        currentRowData.kelas?.idKelas
      );
    }
  }, [currentRowData, form]);

  useEffect(() => {
    const semesterId = form.getFieldValue("idSemester");
    const kelasId = form.getFieldValue("idKelas");
    filterSeasonOptions(semesterId, kelasId);
  }, [seasonList]);

  useEffect(() => {
    if (userSchoolId) {
      form.setFieldsValue({ idStudyProgram: userSchoolId });
    }
  }, [userSchoolId, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const payload = {
        idBankSoal: values.idBankSoal,
        idSoalUjian: currentRowData?.idSoalUjian,
        namaUjian: currentRowData?.namaUjian,
        pertanyaan: currentRowData?.pertanyaan,
        bobot: currentRowData?.bobot?.toString(),
        jenisSoal: currentRowData?.jenisSoal,
        idStudyProgram: values.idStudyProgram,
        idSubject: values.idSubject,
        idRpsDetail: values.idRpsDetail,
        idTahun: values.idTahun,
        idSemester: values.idSemester,
        idKelas: values.idKelas,
        idSeason: values.idSeason,
        idTaksonomi: currentRowData?.taksonomi?.idTaksonomi,
        idSchool: values.idStudyProgram,
      };

      const jenisSoal = currentRowData?.jenisSoal;
      switch (jenisSoal) {
        case "PG":
          payload.opsi = currentRowData?.opsi || {};
          payload.jawabanBenar = currentRowData?.jawabanBenar || [];
          break;
        case "MULTI":
          payload.opsi = currentRowData?.opsi || {};
          payload.jawabanBenar = currentRowData?.jawabanBenar || [];
          break;
        case "COCOK":
          payload.pasangan = currentRowData?.pasangan || {};
          payload.jawabanBenar = currentRowData?.jawabanBenar || {};
          break;
        case "ISIAN":
          payload.jawabanBenar = currentRowData?.jawabanBenar || [""];
          payload.toleransiTypo = currentRowData?.toleransiTypo || null;
          break;
      }

      onOk(payload);
    } catch (error) {
      console.error("Validation failed:", error);
    }
  };

  return (
    <Modal
      title="Edit Informasi Bank Soal"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      width={1000}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col xs={24} sm={24} md={24}>
            <Form.Item name="idBankSoal" style={{ display: "none" }}>
              <Input type="hidden" />
            </Form.Item>

            <Form.Item
              label="Study Program:"
              name="idStudyProgram"
              style={{ display: "none" }}
              rules={[{ required: true, message: "Silahkan pilih Study Program" }]}
            >
              <Select defaultValue={userSchoolId} disabled>
                {userSchoolId && <Option key={userSchoolId} value={userSchoolId}>{userSchoolId}</Option>}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item
              label="Subject:"
              name="idSubject"
              rules={[{ required: true, message: "Silahkan pilih Subject" }]}
            >
              <Select placeholder="Pilih Subject" showSearch optionFilterProp="children">
                {subjectList.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item
              label="Tahun Ajaran:"
              name="idTahun"
              rules={[{ required: true, message: "Silahkan pilih Tahun Ajaran" }]}
            >
              <Select placeholder="Pilih Tahun Ajaran" showSearch optionFilterProp="children">
                {tahunAjaranList.map((item) => (
                  <Option key={item.idTahun} value={item.idTahun}>
                    {item.tahunAjaran}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item
              label="Semester:"
              name="idSemester"
              rules={[{ required: true, message: "Silahkan pilih Semester" }]}
            >
              <Select
                placeholder="Pilih Semester"
                showSearch
                optionFilterProp="children"
                onChange={(value) => {
                  const kelasId = form.getFieldValue("idKelas");
                  form.setFieldsValue({ idSeason: undefined });
                  filterSeasonOptions(value, kelasId);
                }}
              >
                {semesterList.map((item) => (
                  <Option key={item.idSemester} value={item.idSemester}>
                    {item.namaSemester}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item
              label="Kelas:"
              name="idKelas"
              rules={[{ required: true, message: "Silahkan pilih Kelas" }]}
            >
              <Select
                placeholder="Pilih Kelas"
                showSearch
                optionFilterProp="children"
                onChange={(value) => {
                  const semesterId = form.getFieldValue("idSemester");
                  form.setFieldsValue({ idSeason: undefined });
                  filterSeasonOptions(semesterId, value);
                }}
              >
                {kelasList.map((item) => (
                  <Option key={item.idKelas} value={item.idKelas}>
                    {item.namaKelas}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item label="Season (Opsional):" name="idSeason">
              <Select
                placeholder="Pilih Season"
                showSearch
                optionFilterProp="children"
                allowClear
              >
                {(filteredSeasonList.length > 0 ? filteredSeasonList : seasonList).map((item) => (
                  <Option key={item.idSeason} value={item.idSeason}>
                    {(item.kelas?.namaKelas || "-") + " | " + (item.semester?.namaSemester || "-")}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={12}>
            <Form.Item
              label="RPS Detail:"
              name="idRpsDetail"
              rules={[{ required: true, message: "Silahkan pilih RPS Detail" }]}
            >
              <Select placeholder="Pilih RPS Detail" showSearch optionFilterProp="children">
                {rpsDetailList.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.weekLabel ? `${item.weekLabel} - ` : ""}
                    {item.sub_cp_mk || item.id}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          {creatorInfo && (
            <Col xs={24} sm={24} md={24}>
              <Form.Item label="Dibuat oleh:">
                <Input value={creatorInfo} disabled style={{ color: "#666" }} />
              </Form.Item>
            </Col>
          )}
        </Row>
      </Form>
    </Modal>
  );
};

export default EditBankSoalForm;
